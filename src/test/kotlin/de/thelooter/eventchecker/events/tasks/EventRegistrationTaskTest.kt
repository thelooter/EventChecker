package de.thelooter.eventchecker.events.tasks

import io.github.classgraph.ClassGraph
import io.github.classgraph.ClassInfo
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageEvent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

internal class EventRegistrationTaskTest {

    private lateinit var eventRegistrationTask: EventRegistrationTask

    @BeforeEach
    fun setUp() {
        val event = ClassGraph()
            .enableClassInfo()
            .scan()
            .getClassInfo(Event::class.java.getName())
            .subclasses
            .filter { info: ClassInfo -> !info.isAbstract }
            .filter { info: ClassInfo -> info.name == "org.bukkit.event.entity.EntityDamageEvent" }
            .first()

        eventRegistrationTask = EventRegistrationTask(event)
    }

    @Test
    @DisplayName("Test Event Type")
    fun testgetEventType() {
        assertEquals(
            "org.bukkit.event.entity.EntityDamageEvent",
            eventRegistrationTask.getEventType().name
        )
    }

    @Test
    @DisplayName("Test Event Priority")
    fun testgetEventPriority() {
        assertEquals(
            EventPriority.NORMAL,
            eventRegistrationTask.getEventPriority()
        )
    }

    @Test
    @DisplayName("Test Task Type")
    fun testgetTaskType() {
        assertEquals(
            "registration",
            eventRegistrationTask.getTaskType()
        )
    }

    @Test
    @DisplayName("Test Task State")
    fun testGetStateDefault() {
        assertEquals(
            EventTask.State.SCHEDULED,
            eventRegistrationTask.getTaskState()
        )
    }
}