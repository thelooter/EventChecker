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
    ): List<String> {

        args?.let { it ->

            if (it.size == 1) {
                return listOf("reload", "list")
            }

        if (args.size == 2 && args[0] == "list") {
            return listOf("all")
        }

        if (args.size == 3 && args[0] == "list" && args[1] == "all") {
            val pages: MutableList<Int> = ArrayList()
            for (i in Lists.partition<String>(EventChecker.eventNames, 50).indices) {
                pages.add(i + 1)
            if (it.size == 2) {
                if (it[0] == "list") {
                    return listOf("all", "blacklist", "whitelist")
                }
            }

            if (it.size == 3) {
                if (it[0] == "list") {
                    val pageSize = 25
                    return when (it[1]) {
                        "all" -> createPageList(EventChecker.eventNames, pageSize)
                        "blacklist" -> {
                            createPageList(EventChecker.instance.config.getStringList("excluded-events"), pageSize)
                        }
                        "whitelist" -> {
                            createPageList(EventChecker.instance.config.getStringList("included-events"), pageSize)
                        }
                        else -> emptyList()
                    }
                }

            }
        }
        return emptyList()
    }
}


/**
 * Creates a list of page numbers based on a given list and page size.
 *
 * @param list The input list of elements.
 * @param pageSize The number of elements per page.
 * @return The list of page numbers as strings.
 */
fun createPageList(list: List<String>, pageSize: Int): List<String> {
    return Lists.partition(list, pageSize).indices.map { (it + 1).toString() }
}