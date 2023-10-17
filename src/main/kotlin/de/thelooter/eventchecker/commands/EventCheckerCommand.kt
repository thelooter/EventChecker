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

        val simpleEventNames = EventChecker.instance.eventNames.map {
            it.substring(it.lastIndexOf(".") + 1)
        }

        if (!sender.hasPermission("eventchecker.admin")) {
            return true
        }

        if (args?.isEmpty() == true) {
            sender.sendMessage("§cUsage: /eventchecker <reload|list|disable|enable>")
            return false
        }

        if (args!!.size == 1) {
            if (args[0] == "list") {
                sender.sendMessage("§cUsage: /eventchecker list <all|blacklist|whitelist> <page>")
                return true
            }

            if (args[0] == "disable") {
                sender.sendMessage("§cUsage: /eventchecker disable <event>")
                return true
            }

            if (args[0] == "enable") {
                sender.sendMessage("§cUsage: /eventchecker enable <event>")
                return true
            }
        }

        if (args.size == 2) {
            if (args[0] == "list") {
                if (args[1] == "all") {
                    sender.sendMessage("§cUsage: /eventchecker list all <page>")
                    return true
                }

                if (args[1] == "blacklist") {
                    sender.sendMessage("§cUsage: /eventchecker list blacklist <page>")
                    return true
                }

                if (args[1] == "whitelist") {
                    sender.sendMessage("§cUsage: /eventchecker list whitelist <page>")
                    return true
                }
            }

            if (args[0] == "disable") {
                if (!EventChecker.instance.eventNames.contains(args[1]) || simpleEventNames.contains(args[1])) {
                    sender.sendMessage("§cThis event does not exist!")
                    return true
                }

                //TODO: Disable event
            }

            if (args[0] == "enable") {
                if (!EventChecker.instance.eventNames.contains(args[1]) || simpleEventNames.contains(args[1])) {
                    sender.sendMessage("§cThis event does not exist!")
                    return true
                }

                //TODO: Enable event
            }
        }

        if (args[0] == "list") {

            val pageSize = 25

            if (args[1] == "all") {
                val partition = Lists.partition<String>(EventChecker.instance.eventNames, pageSize)
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
            if (args[1] == "blacklist") {
                if (!EventChecker.instance.config.getBoolean("blacklist", false)) {
                    sender.sendMessage("§cThe blacklist is disabled!")
                    return true
                }
                val partition = Lists.partition(
                    EventChecker.instance.config.getStringList("excluded-events"),
                    pageSize
                )

                val page = args[2].toInt()

                if (page > partition.size + 1) {
                    sender.sendMessage("§cThis page does not exist!")
                    return true
                }

                sender.sendMessage("§7Blacklisted Events (Page $page):")
                partition[page - 1].forEach(Consumer { event: String ->
                    sender.sendMessage(
                        "§8- §7$event"
                    )
                })

                return true
            }

            if (args[1] == "whitelist") {
                if (!EventChecker.instance.config.getBoolean("whitelist", false)) {
                    sender.sendMessage("§cThe whitelist is disabled!")
                    return true
                }
                val partition = Lists.partition(
                    EventChecker.instance.config.getStringList("included-events"),
                    pageSize
                )

                val page = args[2].toInt()

                if (page > partition.size + 1) {
                    sender.sendMessage("§cThis page does not exist!")
                    return true
                }

                sender.sendMessage("§7Whitelisted Events (Page $page):")
                partition[page - 1].forEach(Consumer { event: String ->
                    sender.sendMessage(
                        "§8- §7$event"
                    )
                })

                return true
            }
        }

        return true
    }

}