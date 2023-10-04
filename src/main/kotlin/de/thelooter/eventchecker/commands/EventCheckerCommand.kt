package de.thelooter.eventchecker.commands

import com.google.common.collect.Lists
import de.thelooter.eventchecker.EventChecker
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import java.util.function.Consumer

class EventCheckerCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {

        if (sender.hasPermission("eventchecker.admin")) {
            return true
        }

        if (args?.isEmpty() == true) {
            sender.sendMessage("§cUsage: /eventchecker <reload|list>")
            return false
        }

        if (args!!.size == 1 && args[0] == "list") {
            sender.sendMessage("§cUsage: /eventchecker list <all> <page>")
            return true
        }

        if (args.size == 2 && args[0] == "list" && args[1] == "all") {
            sender.sendMessage("§cUsage: /eventchecker list all <page>")
            return true
        }

        if (args[0] == "list" && args[1] == "all") {
            val partition = Lists.partition<String>(EventChecker.eventNames, 50)
            val page = args[2].toInt()
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

        return true
    }

}