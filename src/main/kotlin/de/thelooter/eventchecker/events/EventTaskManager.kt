package de.thelooter.eventchecker.events

import de.thelooter.eventchecker.events.tasks.EventTask
import kotlinx.coroutines.runBlocking
import org.bukkit.event.Event
import org.bukkit.event.EventPriority

class EventTaskManager {

    private val tasks: ArrayDeque<EventTask> = ArrayDeque()


    /**
     * Adds a new Task to the TaskManager
     * @param task The Task that should be added
     * @since 1.3.0
     */
    fun addTask(task: EventTask) {
        tasks.addLast(task)
    }

    /**
     * Removes a Task from the TaskManager
     * @param task The Task that should be removed
     * @return True if the Task was removed, false if not
     */
    fun removeTask(task: EventTask): Boolean {
        return tasks.remove(task)
    }

    /**
     * Gets all Tasks from the TaskManager
     * @return All Tasks
     * @since 1.3.0
     */
    fun getTasks(): List<EventTask> {
        return tasks
    }

    /**
     * Gets all Tasks from the TaskManager filtered by the Type
     * @return All Tasks with the given Type
     * @since 1.3.0
     */
    fun getTasksByType(type: String): List<EventTask> {
        return tasks.filter { task -> task.getTaskType() == type }
    }

    /**
     * Gets a selection of Tasks from the TaskManager filtered by the [Event]
     * @return All Tasks with the given [Event]
     * @since 1.3.0
     */
    fun getTasksByEvent(event: Class<out Event>): List<EventTask> {
        return tasks.filter { task -> task.getEventType() == event }
    }

    /**
     * Gets a selection of Tasks from the TaskManager filtered by the [EventPriority]
    * @return All Tasks with the given [EventPriority]
     */
    fun getTasksByPriority(priority: EventPriority): List<EventTask> {
        return tasks.filter { task -> task.getEventPriority() == priority }
    }

    /**
     * Processes all Tasks
     * @since 1.3.0
     */
    fun processTasks() {
        while (tasks.isNotEmpty()) {
            val task = tasks.removeFirst()
            runBlocking {
                task.execute()
            }
        }
    }
}