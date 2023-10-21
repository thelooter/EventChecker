package de.thelooter.eventchecker.events.tasks

import be.seeseemelk.mockbukkit.MockBukkit
import de.thelooter.eventchecker.EventChecker
import io.github.classgraph.ClassGraph
import io.github.classgraph.ClassInfo
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.bukkit.entity.Entity
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageEvent
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.lang.StringBuilder

internal class EventRegistrationTaskTest {

    private lateinit var eventRegistrationTask: EventRegistrationTask

    @BeforeEach
    fun setUp() {
        MockBukkit.mock()
        MockBukkit.load(EventChecker::class.java)
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

    @AfterEach
    fun tearDown() {
        MockBukkit.unmock()
    }

    @Test
    @DisplayName("Test Event Type")
    fun testgetEventType() {
        assertEquals(
            "org.bukkit.event.entity.EntityDamageEvent",
            eventRegistrationTask.getEventType()?.name
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

    @Test
    fun testGetEventTypeWrongType() {
        val event = ClassGraph()
            .enableClassInfo()
            .scan()
            .getClassInfo(ClassInfo::class.java.name)

        println(event)

        val eventRegistrationTask = EventRegistrationTask(event)

        assertNull(eventRegistrationTask.getEventType())
    }

    @Test
    fun testExecuteWrongType() {
        val event = ClassGraph()
            .enableClassInfo()
            .scan()
            .getClassInfo(ClassInfo::class.java.name)

        println(event)

        val eventRegistrationTask = EventRegistrationTask(event)

        runBlocking {
            val one = async { eventRegistrationTask.execute() }
            one.await()

            assertEquals(
                EventTask.State.FAILED,
                eventRegistrationTask.getTaskState()
            )
        }
    }


}