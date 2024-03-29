package de.thelooter.eventchecker

import de.thelooter.eventchecker.commands.EventCheckerCommand
import de.thelooter.eventchecker.commands.complete.EventCheckerCommandCompleter
import de.thelooter.eventchecker.events.EventTaskManager
import de.thelooter.eventchecker.events.tasks.EventRegistrationTask
import io.github.classgraph.ClassGraph
import io.github.classgraph.ClassInfo
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.*

open class EventChecker : JavaPlugin() {

    companion object {
        var eventNames: MutableList<String> = ArrayList()
        lateinit var instance: JavaPlugin
        val eventTaskManager = EventTaskManager()
    }

    /**
     * This method is the onEnable() method of the EventChecker class. It is called when the plugin is enabled.
     * It performs various setup and initialization tasks.
     */
    override fun onEnable() {
        instance = this
        val events = ClassGraph()
            .enableClassInfo()
            .scan()
            .getClassInfo(Event::class.java.name)
            .subclasses
            .filter { info: ClassInfo -> !info.isAbstract }

        events.forEach {
            eventNames.add(it.name)
        }

        val configFile = File(dataFolder, "config.yml").apply {
            if (!exists()) {
                parentFile.mkdirs()
                saveDefaultConfig()
            }
        }


        val config = YamlConfiguration().apply {
            try {
                load(configFile)
            } catch (e: Exception) {
                logger.severe("Could not load config.yml: ${ExceptionUtils.getStackTrace(e)}")
            }
        }


        val blackList = config.getBoolean("blacklist", false)
        val excludedEvents = config.getStringList("excluded-events")

        val whiteList = config.getBoolean("whitelist", false)
        val includedEvents = config.getStringList("included-events")

        if (blackList && whiteList) {
            logger.severe("Both blacklist and whitelist are enabled. Please disable one of them.")
            return
        }

        val enabledEventsBlackList: List<ClassInfo> = events.filter {
            !excludedEvents.contains(it.name.substring(it.name.lastIndexOf(".") + 1))
        }

        val enabledEventsWhiteList: List<ClassInfo> = events.filter {
            includedEvents.contains(it.name.substring(it.name.lastIndexOf(".") + 1))
        }

        if (blackList) {
            this registerEvents enabledEventsBlackList
        } else if (whiteList) {
            this registerEvents enabledEventsWhiteList
        } else {
            this registerEvents events
        }

        logger.info("HandlerList size: " + HandlerList.getHandlerLists().size)

        getCommand("eventchecker")?.apply {
            setExecutor(EventCheckerCommand())
            tabCompleter = EventCheckerCommandCompleter()
        }
    }

    /**
     * Registers all events in the given list.
     * @param enabledEvent The list of events to register.
     *
     * @since 1.3.0
     */
    private infix fun registerEvents(enabledEvent: List<ClassInfo>) {
        enabledEvent.forEach {
            eventTaskManager.addTask(EventRegistrationTask(it))
        }

        eventTaskManager.processTasks()

        val eventNames = enabledEvent.map {
            it.name.substring(it.name.lastIndexOf(".") + 1)
        }
        logger.info("Enabled events: " + eventNames.joinToString(", "))
        logger.info("Number of enabled events: " + eventNames.size)
    }

}
