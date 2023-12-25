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
                sender.sendMessage("§cUsage: /eventchecker list <all> <page>")
                return true
            }

            if (it.size == 2 && it[0] == "list" && it[1] == "all") {
                sender.sendMessage("§cUsage: /eventchecker list all <page>")
                return true
            }

            if (it[0] == "list" && it[1] == "all") {
                val partition = Lists.partition<String>(EventChecker.eventNames, 50)
                val page = it[2].toInt()
                if (page > partition.size + 1) {
                    sender.sendMessage("§cThis page does not exist!")
                    return true
                }
                sender.sendMessage("§7Events (Page $page):")
                partition[page - 1].forEach(Consumer { event: String ->
                    sender.sendMessage(
                        "§8- §7$event"
                    )
                })
            }
        }

        return true
    }

}