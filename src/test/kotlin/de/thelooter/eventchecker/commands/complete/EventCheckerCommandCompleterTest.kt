package de.thelooter.eventchecker.commands.complete

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.MockBukkitExtension
import be.seeseemelk.mockbukkit.MockBukkitInject
import be.seeseemelk.mockbukkit.ServerMock
import be.seeseemelk.mockbukkit.entity.PlayerMock
import de.thelooter.eventchecker.EventChecker
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@Suppress("unstableApiUsage")
@ExtendWith(MockBukkitExtension::class)
internal class EventCheckerCommandCompleterTest {

    @MockBukkitInject
    private lateinit var server: ServerMock
    private lateinit var player: PlayerMock

    @BeforeEach
    fun setUp() {
        MockBukkit.load(EventChecker::class.java)
        player = server.addPlayer()
    }

    @Test
    fun testCompleteNoArgs() {
        val commandTabComplete = server.getCommandTabComplete(player, "eventchecker ")
        assertTrue(commandTabComplete.contains("reload"))
        assertTrue(commandTabComplete.contains("list"))
    }

    @Test
    fun testCompleteList() {
        val commandTabComplete = server.getCommandTabComplete(player, "eventchecker list ")
        assertTrue(commandTabComplete.contains("all"))
    }

    @Test
    fun testCompleteListAll() {
        val commandTabComplete = server.getCommandTabComplete(player, "eventchecker list all ")
        assertTrue(commandTabComplete.contains("1"))
    }

    @Test
    fun testCompleteListAllPage() {
        val commandTabComplete = server.getCommandTabComplete(player, "eventchecker list all 1 ")
        assertTrue(commandTabComplete.isEmpty())
    }

    @Test
    fun testComplete2ArgsNotList() {
        val commandTabComplete = server.getCommandTabComplete(player, "eventchecker reload ")
        assertTrue(commandTabComplete.isEmpty())
    }

    @Test
    fun testComplete3ArgsNotList() {
        val commandTabComplete = server.getCommandTabComplete(player, "eventchecker reload 1 ")
        assertTrue(commandTabComplete.isEmpty())
    }

    @Test
    fun testComplete3ArgsNotAll() {
        val commandTabComplete = server.getCommandTabComplete(player, "eventchecker list test 2 ")
        assertTrue(commandTabComplete.isEmpty())
    }

}

