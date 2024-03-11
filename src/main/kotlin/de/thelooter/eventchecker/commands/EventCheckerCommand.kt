package de.thelooter.eventchecker.commands

import com.google.common.collect.Lists
import de.thelooter.eventchecker.EventChecker
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import java.util.function.Consumer

/**
 * The CommandExecutor for the EventChecker Command
 * @see CommandExecutor
 * @since 1.2.0
 */
class EventCheckerCommand : CommandExecutor {
    companion object {
        private var BLACKLIST_KEY = "blacklist"
        private var WHITELIST_KEY = "whitelist"
        private var ALL_KEY = "all"
    }
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {

        args?.let {
            if (!sender.hasPermission("eventchecker.admin")) {
                sender.sendMessage("§cYou don't have the permission to execute the command")
                return true
            }

            if (it.isEmpty()) {
                sender.sendMessage("§cUsage: /eventchecker <reload|list>")
                return false
            }

            if (it.size == 1 && it[0] == "list") {
                sender.sendMessage("§cUsage: /eventchecker list <$ALL_KEY|$BLACKLIST_KEY|$WHITELIST_KEY> <page>")
                return true
            }

            if (it.size == 2 && it[0] == "list") {
                val listType = it[1]
                if (listType == ALL_KEY || listType == BLACKLIST_KEY || listType == WHITELIST_KEY) {
                    return sendListUsageCommand(sender, listType)
                }
            }


            if (it.size == 3 && it[0] == "list") {

                val pageSize = 25

                if (it[1] == ALL_KEY) {
                    val partition = Lists.partition<String>(EventChecker.eventNames, pageSize)
                    val page = it[2].toInt()
                    if (handleNonExistentPage(page, partition, sender)) return true
                    sender.sendMessage("§7Events (Page $page):")
                    sendEvents(partition, page, sender)
                }
                if (it[1] == BLACKLIST_KEY) {
                    if (isListTypeEnabled(sender, BLACKLIST_KEY)) return true
                    val partition = handlePartition(pageSize, "excluded-events")

                    if (handleEmptyPartition(partition, sender, BLACKLIST_KEY)) return true

                    val page = it[2].toInt()

                    if (handleNonExistentPage(page, partition, sender)) return true

                    sender.sendMessage("§7Blacklisted Events (Page $page):")
                    sendEvents(partition, page, sender)

                    return true
                }

                if (it[1] == WHITELIST_KEY) {
                    if (isListTypeEnabled(sender, WHITELIST_KEY)) return true
                    val partition = handlePartition(pageSize, "included-events")

                    if (handleEmptyPartition(partition, sender, WHITELIST_KEY)) return true

                    val page = it[2].toInt()

                    if (handleNonExistentPage(page, partition, sender)) return true

                    sender.sendMessage("§7Whitelisted Events (Page $page):")
                    sendEvents(partition, page, sender)

                    return true
                }

            }
        }
        return true
    }

    private fun handleEmptyPartition(
        partition: List<List<String>>,
        sender: CommandSender,
        listType: String
    ): Boolean {
        if (partition.isEmpty()) {
            sender.sendMessage("§cThe $listType is empty!")
            return true
        }
        return false
    }

    private fun isListTypeEnabled(sender: CommandSender,listType: String): Boolean {
        if (!EventChecker.instance.config.getBoolean(listType, false)) {
            sender.sendMessage("§cThe $listType is disabled!")
            return true
        }
        return false
    }

    private fun handleNonExistentPage(
        page: Int,
        partition: List<List<String>>,
        sender: CommandSender
    ): Boolean {
        if (page > partition.size + 1) {
            sender.sendMessage("§cThis page does not exist!")
            return true
        }
        return false
    }

    private fun sendEvents(
        partition: List<List<String>>,
        page: Int,
        sender: CommandSender
    ) {
        partition[page - 1].forEach(Consumer { event: String ->
            sender.sendMessage(
                "§8- §7$event"
            )
        })
    }

    private fun sendListUsageCommand(sender: CommandSender, listType: String): Boolean {
        sender.sendMessage("§cUsage: /eventchecker list $listType <page>")
        return true
    }

    private fun handlePartition(pageSize: Int, inputList: String): List<List<String>> {
        return Lists.partition(EventChecker.instance.config.getStringList(inputList), pageSize)
    }
}