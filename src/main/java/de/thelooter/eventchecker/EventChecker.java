package de.thelooter.eventchecker;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class EventChecker extends JavaPlugin implements Listener {

  @Override
  public void onEnable() {
    ClassInfoList events =
        new ClassGraph()
            .enableClassInfo()
            .scan()
            .getClassInfo(Event.class.getName())
            .getSubclasses()
            .filter(info -> !info.isAbstract());

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

    List<String> excludedEvents = config.getStringList("excluded-events");

    Listener listener = new Listener() {};

    EventExecutor executor = (ignored, event) -> getLogger().info("Event: " + event.getEventName());

    List<ClassInfo> enabledEvents =
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

    try {
      for (ClassInfo info : enabledEvents) {

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
      }
    } catch (ClassNotFoundException e) {
        getLogger().severe("Could not load event: " + ExceptionUtils.getStackTrace(e));
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
  }
}