package de.thelooter.eventchecker

import org.mockbukkit.mockbukkit.MockBukkit
import org.mockbukkit.mockbukkit.ServerMock
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class EventCheckerTest {
    private var server: ServerMock? = null
    private var eventChecker: EventChecker? = null

    @BeforeEach
    fun setUp() {
        server = MockBukkit.mock()
        eventChecker = MockBukkit.load(EventChecker::class.java)
    }

    @AfterEach
    fun tearDown() {
        MockBukkit.unmock()
    }

    @Test
    fun test() {
        Assertions.assertNotNull(eventChecker)
        Assertions.assertTrue(eventChecker!!.isEnabled)
        Assertions.assertInstanceOf(EventChecker::class.java, eventChecker)
    }
}