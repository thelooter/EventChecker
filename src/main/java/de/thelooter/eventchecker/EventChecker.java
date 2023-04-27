package de.thelooter.eventchecker;

import de.thelooter.eventchecker.commands.EventCheckerCommand;
import de.thelooter.eventchecker.commands.complete.EventCheckerCommandCompleter;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class EventChecker extends JavaPlugin implements Listener {

  public static List<String> eventNames = new ArrayList<>();

  @Override
  public void onEnable() {
    ClassInfoList events =
        new ClassGraph()
            .enableClassInfo()
            .scan()
            .getClassInfo(Event.class.getName())
            .getSubclasses()
            .filter(info -> !info.isAbstract());

    events.forEach(event -> eventNames.add(event.getName()));

    File configFile = new File(getDataFolder(), "config.yml");
    if (!configFile.exists()) {
      configFile.getParentFile().mkdirs();
      saveDefaultConfig();
    }

    FileConfiguration config = new YamlConfiguration();

    try {
      config.load(configFile);
    } catch (Exception e) {
      getLogger().severe("Could not load config file: " + ExceptionUtils.getStackTrace(e));
    }

    boolean blacklist = config.getBoolean("blacklist", false);
    List<String> excludedEvents = config.getStringList("excluded-events");

    boolean whitelist = config.getBoolean("whitelist", false);
    List<String> includedEvents = config.getStringList("included-events");

    if (blacklist && whitelist) {
      getLogger().severe("Cannot use both blacklist and whitelist at the same time");
      Bukkit.getPluginManager().disablePlugin(this);
      return;
    }

    Listener listener = new Listener() {};

    EventExecutor executor = (ignored, event) -> getLogger().info("Event: " + event.getEventName());

    List<ClassInfo> enabledEventsBlackList =
        events.stream()
            .filter(
                info -> {
                  for (String excludedEvent : excludedEvents) {
                    if (info.getName()
                        .substring(info.getName().lastIndexOf('.') + 1)
                        .equals(excludedEvent)) {
                      return false;
                    }
                  }
                  return true;
                })
            .toList();

    List<ClassInfo> enabledEventsWhiteList =
        events.stream()
            .filter(
                info -> {
                  for (String includedEvent : includedEvents) {
                    if (info.getName()
                        .substring(info.getName().lastIndexOf('.') + 1)
                        .equals(includedEvent)) {
                      return true;
                    }
                  }
                  return false;
                })
            .toList();

    if (blacklist) {
      registerEvents(listener, executor, enabledEventsBlackList);
    } else if (whitelist) {
      registerEvents(listener, executor, enabledEventsWhiteList);
    } else {
      registerEvents(listener, executor, events);
    }

    String[] eventNames =
        events.stream()
            .map(
                classInfo ->
                    classInfo.getName().substring(classInfo.getName().lastIndexOf('.') + 1))
            .toArray(String[]::new);

    getLogger().info("List of Events: " + String.join(", ", eventNames));
    getLogger().info("Number of Events: " + events.size());
    getLogger().info("HandlerList Size:" + HandlerList.getHandlerLists().size());

    // Register Commands
    Objects.requireNonNull(getCommand("eventchecker")).setExecutor(new EventCheckerCommand());

    // Register CommandCompletions
    Objects.requireNonNull(getCommand("eventchecker")).setTabCompleter(new EventCheckerCommandCompleter());
  }

  private void registerEvents(
      Listener listener, EventExecutor executor, List<ClassInfo> enabledEvent) {
    for (ClassInfo info : enabledEvent) {
      try {
        @SuppressWarnings("unchecked")
        Class<? extends Event> eventClass = (Class<? extends Event>) Class.forName(info.getName());

        if (Arrays.stream(eventClass.getDeclaredMethods())
            .anyMatch(
                method ->
                    method.getParameterCount() == 0 && method.getName().equals("getHandlers"))) {
          getServer()
              .getPluginManager()
              .registerEvent(eventClass, listener, EventPriority.NORMAL, executor, this);
        }
      } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
      }
    }
  }
}