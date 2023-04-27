package de.thelooter.eventchecker.commands;

import com.google.common.collect.Lists;
import de.thelooter.eventchecker.EventChecker;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EventCheckerCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
                             @NotNull String[] args) {

        if (!sender.hasPermission("eventchecker.admin")) {
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("§cUsage: /eventchecker <reload|list>");
            return true;
        }

        if (args.length == 1 && args[0].equals("list")) {
            sender.sendMessage("§cUsage: /eventchecker list <all> <page>");
            return true;
        }

        if (args.length == 2 && args[0].equals("list") && args[1].equals("all")) {
            sender.sendMessage("§cUsage: /eventchecker list all <page>");
            return true;
        }

        if (args[0].equals("list") && args[1].equals("all")) {
            List<List<String>> partition = Lists.partition(EventChecker.EVENT_NAMES, 50);
            int page = Integer.parseInt(args[2]);

            if (page > partition.size() + 1) {
                sender.sendMessage("§cThis page does not exist!");
                return true;
            }

            sender.sendMessage("§7Events (Page " + page + "):");
            partition.get(page - 1).forEach(event -> sender.sendMessage("§8- §7" + event));
        }

        return true;
    }
}
