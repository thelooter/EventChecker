package de.thelooter.eventchecker.events.tasks

import org.bukkit.event.Event
import org.bukkit.event.EventPriority

interface EventTask {

    /**
     * Gets the Type of the Event that should be used
     * @return The Type of the Event
     * @since 1.3.0
     */
    fun getEventType(): Class<out Event>

    /**
     * Gets the EventPriority that should be used
     * @return The EventPriority
     * @since 1.3.0
     */
    fun getEventPriority(): EventPriority

    /**
     * Gets the Type of task that should be performed
     * @return The EventExecutor
     * @since 1.3.0
     */
    fun getTaskType(): String

    /**
     * The Code that should be executed
     * @since 1.3.0
     */
    suspend fun execute()

    /**
     * Gets the State of the Task
     * @return The State of the Task
     * @since 1.3.0
     */
    fun getTaskState(): State

    enum class State {
        SCHEDULED, RUNNING, FINISHED
    }
}