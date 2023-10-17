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

        if (args.size == 2 && args[0] == "list") {
            return listOf("all", "blacklist", "whitelist")
        }

        if (args.size == 3 && args[0] == "list") {
            val pageSize = 25
            if (args[1] == "all") {
                val pages: MutableList<Int> = ArrayList()
                for (i in Lists.partition<String>(EventChecker.instance.eventNames, pageSize).indices) {
                    pages.add(i + 1)
                }
                return pages.stream().map { obj: Int? -> "$obj"}.toList()
            }

            if (args[1] == "blacklist") {
                val pages: MutableList<Int> = ArrayList()
                for (i in Lists.partition<String>(EventChecker.instance.config.getStringList("excluded-events"),
                    pageSize).indices) {
                    pages.add(i + 1)
                }
                return pages.stream().map { obj: Int? -> "$obj"}.toList()
            }

            if (args[1] == "whitelist") {
                val pages: MutableList<Int> = ArrayList()
                for (i in Lists.partition<String>(EventChecker.instance.config.getStringList("included-events"),
                    pageSize).indices) {
                    pages.add(i + 1)
                }
                return pages.stream().map { obj: Int? -> "$obj"}.toList()
            }
        }

        return emptyList()
    }
}