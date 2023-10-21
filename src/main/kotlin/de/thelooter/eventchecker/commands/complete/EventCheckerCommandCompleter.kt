package de.thelooter.eventchecker.commands.complete

import com.google.common.collect.Lists
import de.thelooter.eventchecker.EventChecker
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

/**
 * The TabCompleter for the EventChecker Command
 * @see TabCompleter
 * @since 1.2.0
 */
class EventCheckerCommandCompleter : TabCompleter {
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>?
    ): List<String>? {
        if (args!!.size == 1) {
            return listOf("reload", "list")
        }

        if (args.size == 2) {
            if (args[0] == "list") {
                return listOf("all")
            }
        }

        if (args.size == 3) {
            if (args[0] == "list") {
                if (args[1] == "all") {
                    val pages: MutableList<Int> = ArrayList()
                    for (i in Lists.partition<String>(EventChecker.eventNames, 50).indices) {
                        pages.add(i + 1)
                    }
                    return pages.stream().map { obj: Int? ->
                        java.lang.String.valueOf(
                            obj
                        )
                    }.toList()
                }
            }
        }

        return emptyList<String>()
    }
}