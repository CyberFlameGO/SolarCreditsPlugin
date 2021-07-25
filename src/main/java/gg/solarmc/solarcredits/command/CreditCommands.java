package gg.solarmc.solarcredits.command;

import gg.solarmc.solarcredits.SolarCredit;
import gg.solarmc.solarcredits.command.commands.*;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CreditCommands implements CommandExecutor {
    private final List<CreditSubCommand> subCommands;
    private final CommandHelper helper;

    public CreditCommands(SolarCredit plugin, CommandHelper helper, String tebexSecret) {
        subCommands = List.of(
                new AddCommand(),
                new BalanceCommand(plugin),
                new ReloadCommand(plugin),
                new RemoveCommand(),
                new SendCommand(plugin),
                new SetCommand(),
                new ShopCommand(plugin),
                new SpendCommand(plugin, tebexSecret, helper.getMessageConfig("spend"))
        );
        this.helper = helper;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            String commands = "Solar Credits Commands : \n" +
                    subCommands.stream()
                            .filter(it -> {
                                String permission = command.getPermission();
                                if (permission == null) return true;
                                return sender.hasPermission(permission);
                            })
                            .map(it -> ChatColor.BOLD + it.getName() + ChatColor.RESET + " " + it.getArgs() + " : " + it.getDescription())
                            .collect(Collectors.joining("\n")) +
                    "\n/credits <command>";
            sender.sendMessage(commands.split("\n"));
            return true;
        }

        String arg = args[0];
        CreditSubCommand subCommand = getCommand(arg);

        if (subCommand != null) {
            List<String> subArgs = Arrays.asList(args).subList(1, args.length);

            subCommand.execute(sender, subArgs.toArray(String[]::new), this.helper);
            return true;
        } else
            sender.sendMessage("No Command for " + arg + " in credits");
        return true;
    }

    private CreditSubCommand getCommand(String name) {
        return this.subCommands.stream()
                .filter(cmd -> cmd.getName().equals(name.toLowerCase()))
                .findFirst()
                .orElse(null);
    }
}
