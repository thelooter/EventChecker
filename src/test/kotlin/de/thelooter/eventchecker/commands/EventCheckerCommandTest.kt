package de.thelooter.eventchecker.commands

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.entity.PlayerMock
import de.thelooter.eventchecker.EventChecker
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class EventCheckerCommandTest {

    private lateinit var player : PlayerMock

    @BeforeEach
    fun setUp() {
        val server = MockBukkit.mock()
        MockBukkit.load(EventChecker::class.java)
        player = server.addPlayer()
    }

    @AfterEach
    fun tearDown() {
        MockBukkit.unmock()
    }

    @Test
    fun testNoPerm() {
        player.performCommand("eventchecker")
        player.assertSaid("§cYou don't have the permission to execute the command")
    }

    @Test
    fun testNoArgs() {
        player.addAttachment(EventChecker.instance, "eventchecker.admin", true)
        player.performCommand("eventchecker")
        player.assertSaid("§cUsage: /eventchecker <reload|list>")
    }

    @Test
    fun testList() {
        player.addAttachment(EventChecker.instance, "eventchecker.admin", true)
        player.performCommand("eventchecker list")
        player.assertSaid("§cUsage: /eventchecker list <all|blacklist|whitelist> <page>")
    }

    @Test
    fun testListAll() {
        player.addAttachment(EventChecker.instance, "eventchecker.admin", true)
        player.performCommand("eventchecker list all")
        player.assertSaid("§cUsage: /eventchecker list all <page>")
    }

    @Test
    fun testListAllPage() {
        player.addAttachment(EventChecker.instance, "eventchecker.admin", true)
        player.performCommand("eventchecker list all 1")
        player.assertSaid("§7Events (Page 1):")
        Assertions.assertNotNull(player.nextMessage())
    }

    @Test
    fun testListAllPages_PageToBig() {
        player.addAttachment(EventChecker.instance, "eventchecker.admin", true)
        player.performCommand("eventchecker list all 1000")
        player.assertSaid("§cThis page does not exist!")
    }

    @Test
    fun testListBlacklist() {
        player.addAttachment(EventChecker.instance, "eventchecker.admin", true)
        player.performCommand("eventchecker list blacklist")
        player.assertSaid("§cUsage: /eventchecker list blacklist <page>")
    }

    @Test
    fun testListWhitelist() {
        player.addAttachment(EventChecker.instance, "eventchecker.admin", true)
        player.performCommand("eventchecker list whitelist")
        player.assertSaid("§cUsage: /eventchecker list whitelist <page>")
    }

    @Test
    fun testListBlacklistPage() {
        player.addAttachment(EventChecker.instance, "eventchecker.admin", true)
        player.performCommand("eventchecker list blacklist 1")
        player.assertSaid("§cThe blacklist is empty!")
        Assertions.assertNull(player.nextMessage())
    }

}