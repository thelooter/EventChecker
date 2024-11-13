package de.thelooter.eventchecker.commands.complete

import org.mockbukkit.mockbukkit.MockBukkit
import org.mockbukkit.mockbukkit.MockBukkitExtension
import org.mockbukkit.mockbukkit.MockBukkitInject
import org.mockbukkit.mockbukkit.ServerMock
import org.mockbukkit.mockbukkit.entity.PlayerMock
import de.thelooter.eventchecker.EventChecker
import org.bukkit.permissions.PermissionAttachment
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

/**
 * This class contains unit tests for the EventCheckerCommandCompleter class.
 *
 * @suppress("unstableApiUsage")
 * @constructor Creates an instance of EventCheckerCommandCompleterTest
 */
@Suppress("unstableApiUsage")
@ExtendWith(MockBukkitExtension::class)
internal class EventCheckerCommandCompleterTest {

    @MockBukkitInject
    private lateinit var server: ServerMock
    private lateinit var player : PlayerMock

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
        commandTabComplete.forEach { it ->
            assertTrue(it.all { c: Char ->  c.isDigit()})
        }
        assertTrue(commandTabComplete.isNotEmpty())
    }

    @Test
    fun testCompleteListAllPage() {
        val commandTabComplete = server.getCommandTabComplete(player, "eventchecker list all 1 ")
        assertTrue(commandTabComplete.isEmpty())
    }

    @Test
    fun testCompleteListBlacklist() {
        val commandTabComplete = server.getCommandTabComplete(player, "eventchecker list blacklist ")
        assertTrue(commandTabComplete.isEmpty())
    }

    @Test
    fun testCompleteListWhitelist() {
        val commandTabComplete = server.getCommandTabComplete(player, "eventchecker list whitelist ")
        assertTrue(commandTabComplete.isEmpty())
    }

    @Test
    fun testCompleteListInvalidType() {
        val commandTabComplete = server.getCommandTabComplete(player, "eventchecker list invalid ")
        assertTrue(commandTabComplete.isEmpty())
    }
}
