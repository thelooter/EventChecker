package de.thelooter.eventchecker

import de.thelooter.eventchecker.commands.EventCheckerCommand
import de.thelooter.eventchecker.commands.complete.EventCheckerCommandCompleter
import io.github.classgraph.ClassGraph
import io.github.classgraph.ClassInfo
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.plugin.EventExecutor
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.*

open class EventChecker : JavaPlugin() {

    companion object {
        var eventNames: MutableList<String> = ArrayList()
    }

    override fun onEnable() {
        val events = ClassGraph()
            .enableClassInfo()
            .scan()
            .getClassInfo(Event::class.java.getName())
            .subclasses
            .filter { info: ClassInfo -> !info.isAbstract }

        events.forEach {
            eventNames.add(it.name)
        }

        val configFile = File(dataFolder, "config.yml")

        if (!configFile.exists()) {
            configFile.parentFile.mkdirs()
            saveDefaultConfig()
        }

        val config = YamlConfiguration()

        try {
            config.load(configFile)
        } catch (e: Exception) {
            logger.severe("Could not load config.yml: " + ExceptionUtils.getStackTrace(e))
        }

        val blackList = config.getBoolean("blacklist", false)
        val excludedEvents = config.getStringList("excluded-events")

        val whiteList = config.getBoolean("whitelist", false)
        val includedEvents = config.getStringList("included-events")

        if (blackList && whiteList) {
            logger.severe("Both blacklist and whitelist are enabled. Please disable one of them.")
            return
        }

        val listener = object : Listener {}

        val executor = EventExecutor { _: Listener?, event: Event ->
            logger.info(
                "Event: " + event.getEventName()
            )
        }

        val enabledEventsBlackList: List<ClassInfo> = events.filter {
            !excludedEvents.contains(it.name)
        }

        val enabledEventsWhiteList: List<ClassInfo> = events.filter {
            includedEvents.contains(it.name)
        }

        if (blackList) {
            registerEvents(listener, executor, enabledEventsBlackList)
        } else if (whiteList) {
            registerEvents(listener, executor, enabledEventsWhiteList)
        } else {
            registerEvents(listener, executor, events)
        }

        val eventNames = events.map { it.name.substring(it.name.lastIndexOf(".") + 1) }
        logger.info("Enabled events: " + eventNames.joinToString(", "))
        logger.info("Number of enabled events: " + eventNames.size)
        logger.info("HandlerList size: " + HandlerList.getHandlerLists().size)

        getCommand("eventchecker")?.setExecutor(EventCheckerCommand())
        getCommand("eventchecker")?.tabCompleter = EventCheckerCommandCompleter()
    }

    private fun registerEvents(listener: Listener, executor: EventExecutor, enabledEvent: List<ClassInfo>) {
        enabledEvent.forEach {
            try {

                @Suppress("UNCHECKED_CAST")
                val eventClass = Class.forName(it.name) as Class<out Event?>
                if (Arrays.stream(eventClass.declaredMethods).anyMatch { method ->
                        method.parameterCount == 0 && method.name.equals("getHandlers")
                    }) {
                    server.pluginManager.registerEvent(
                        eventClass,
                        listener,
                        EventPriority.NORMAL,
                        executor,
                        this
                    )
                }

            } catch (e: ClassNotFoundException) {
                logger.severe("Could not find class: " + ExceptionUtils.getStackTrace(e))
            }
        }
    }

}
