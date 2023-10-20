package de.thelooter.eventchecker.events

import de.thelooter.eventchecker.events.tasks.EventRegistrationTask
import io.github.classgraph.ClassGraph
import io.github.classgraph.ClassInfo
import nonapi.io.github.classgraph.utils.Assert
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class EventTaskManagerTest {

    private lateinit var eventTaskManager: EventTaskManager

    @BeforeEach
    fun setUp() {
        eventTaskManager = EventTaskManager()
    }

    @Test
    fun testAddTask() {
        val event = ClassGraph()
            .enableClassInfo()
            .scan()
            .getClassInfo(Event::class.java.getName())
            .subclasses
            .filter { info: ClassInfo -> !info.isAbstract }
            .filter { info: ClassInfo -> info.name == "org.bukkit.event.entity.EntityDamageEvent" }
            .first()

        val eventRegistrationTask = EventRegistrationTask(event)
        eventTaskManager.addTask(eventRegistrationTask)

        assertEquals(1, eventTaskManager.getTasks().size)
        assertTrue(eventTaskManager.getTasks().contains(eventRegistrationTask))
    }

    @Test
    fun testGetTasksDefault() {
        assertEquals(0, eventTaskManager.getTasks().size)
    }

    @Test
    fun testGetTasksByType() {
        val event = ClassGraph()
            .enableClassInfo()
            .scan()
            .getClassInfo(Event::class.java.getName())
            .subclasses
            .filter { info: ClassInfo -> !info.isAbstract }
            .filter { info: ClassInfo -> info.name == "org.bukkit.event.entity.EntityDamageEvent" }
            .first()

        val eventRegistrationTask = EventRegistrationTask(event)
        eventTaskManager.addTask(eventRegistrationTask)

        assertEquals(1, eventTaskManager.getTasksByType("registration").size)
        assertTrue(eventTaskManager.getTasksByType("registration").contains(eventRegistrationTask))
    }

    @Test
    @Suppress("UNCHECKED_CAST")
    fun testGetTaskByEvent() {
        val event = ClassGraph()
            .enableClassInfo()
            .scan()
            .getClassInfo(Event::class.java.getName())
            .subclasses
            .filter { info: ClassInfo -> !info.isAbstract }
            .filter { info: ClassInfo -> info.name == "org.bukkit.event.entity.EntityDamageEvent" }
            .first()

        val eventRegistrationTask = EventRegistrationTask(event)
        eventTaskManager.addTask(eventRegistrationTask)

        val loadClass = event.loadClass() as Class<out Event>
        assertEquals(1, eventTaskManager.getTasksByEvent(loadClass).size)
        assertTrue(eventTaskManager.getTasksByEvent(loadClass).contains(eventRegistrationTask))
    }

    @Test
    fun testGetTasksByPriority() {
        val event = ClassGraph()
            .enableClassInfo()
            .scan()
            .getClassInfo(Event::class.java.getName())
            .subclasses
            .filter { info: ClassInfo -> !info.isAbstract }
            .filter { info: ClassInfo -> info.name == "org.bukkit.event.entity.EntityDamageEvent" }
            .first()

        val eventRegistrationTask = EventRegistrationTask(event)
        eventTaskManager.addTask(eventRegistrationTask)

        assertEquals(1, eventTaskManager.getTasksByPriority(EventPriority.NORMAL).size)
        assertTrue(eventTaskManager.getTasksByPriority(EventPriority.NORMAL).contains(eventRegistrationTask))
    }

}