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
    override fun getEventType(): Class<out Event>? {
        if (!event.superclasses.any { it.name == Event::class.java.name }) {
            EventChecker.instance.logger.severe("Class " + event.name + " does not extend " + Event::class.java.name)
            return null;
        }
        return event.loadClass() as Class<out Event>
    }

    override fun getEventPriority(): EventPriority {
        return EventPriority.NORMAL
    }

    override fun getTaskType(): String {
        return "registration"
    }

    override suspend fun execute() {
        this.state = EventTask.State.RUNNING

        if (!event.superclasses.any { it.name == Event::class.java.name }) {
            EventChecker.instance.logger.severe("Class " + event.name + " does not extend " + Event::class.java.name)
            this.state = EventTask.State.FAILED
            return;
        }

        val executor = EventExecutor { _: Listener?, event: Event ->
            EventChecker.instance.logger.info(
                "Event: " + event.getEventName()
            )
        }

        @Suppress("UNCHECKED_CAST")
        val eventClass = event.loadClass() as Class<out Event>
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

        this.state = EventTask.State.FINISHED
    }

    override fun getTaskState(): EventTask.State {
        return this.state
    }

}
