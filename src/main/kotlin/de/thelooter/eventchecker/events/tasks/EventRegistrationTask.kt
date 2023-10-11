package de.thelooter.eventchecker.events.tasks

import de.thelooter.eventchecker.EventChecker
import io.github.classgraph.ClassInfo
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.plugin.EventExecutor
import java.util.*

class EventRegistrationTask(private val event: ClassInfo) : EventTask {

    private val listener = object : Listener {}
    private var state = EventTask.State.SCHEDULED


    @Suppress("UNCHECKED_CAST")
    override fun getEventType(): Class<out Event> {
        return try {
            event.loadClass() as Class<out Event>
        } catch (e: ClassNotFoundException) {
            EventChecker.instance.logger.severe("Could not find class: " + ExceptionUtils.getStackTrace(e))
            Event::class.java
        }
    }

    override fun getEventPriority(): EventPriority {
        return EventPriority.NORMAL
    }

    override fun getTaskType(): String {
        return "registration"
    }

    override fun execute() {
        this.state = EventTask.State.RUNNING
        val executor = EventExecutor { _: Listener?, event: Event ->
            EventChecker.instance.logger.info(
                "Event: " + event.getEventName()
            )
        }

        try {
            @Suppress("UNCHECKED_CAST")
            val eventClass = Class.forName(event.name) as Class<out Event?>
            if (Arrays.stream(eventClass.declaredMethods).anyMatch { method ->
                    method.parameterCount == 0 && method.name == "getHandlers"
                }) {
                EventChecker.instance.server.pluginManager.registerEvent(
                    eventClass,
                    listener,
                    EventPriority.NORMAL,
                    executor,
                    EventChecker.instance
                )
            }

        } catch (e: ClassNotFoundException) {
            EventChecker.instance.logger.severe("Could not find class: " + ExceptionUtils.getStackTrace(e))
        }

        this.state = EventTask.State.FINISHED
    }

    override fun getTaskState(): EventTask.State {
        return this.state
    }

}