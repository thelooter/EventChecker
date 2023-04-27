package de.thelooter.eventchecker.commands.complete;

import com.google.common.collect.Lists;
import de.thelooter.eventchecker.EventChecker;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EventCheckerCommandCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
                                      @NotNull String[] args) {
        if (args.length == 1) {
            return List.of("reload", "list");
        }

        if (args.length == 2) {
            if (args[0].equals("list")) {
                return List.of("all");
            }
        }

        if (args.length == 3) {
            if (args[0].equals("list")) {
                if (args[1].equals("all")) {
                    List<Integer> pages = new ArrayList<>();
                    for (int i = 0; i < Lists.partition(EventChecker.eventNames, 50).size(); i++) {
                        pages.add(i+1);
                    }
                    return pages.stream().map(String::valueOf).toList();
                }

            }
        }

        return Collections.emptyList();
    }
}
