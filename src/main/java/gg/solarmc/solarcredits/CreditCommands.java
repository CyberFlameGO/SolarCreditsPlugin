package gg.solarmc.solarcredits;

import gg.solarmc.solarcredits.command.CommandHelper;
import gg.solarmc.solarcredits.command.CreditSubCommand;
import gg.solarmc.solarcredits.command.commands.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class CreditCommands implements CommandExecutor {
    private final SolarCredit plugin;
    private final List<CreditSubCommand> subCommands;

    public CreditCommands(SolarCredit plugin, String tebexSecret) {
        this.plugin = plugin;
        subCommands = List.of(
                new AddCommand(),
                new BalanceCommand(),
                new ReloadCommand(plugin),
                new RemoveCommand(),
                new SendCommand(plugin),
                new SetCommand(),
                new ShopCommand(plugin),
                new SpendCommand(plugin, tebexSecret)
        );
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            // SEND ALL COMMANDS MSG
            return true;
        }

        final String arg = args[0];
        final CreditSubCommand subCommand = getCommand(arg);

        if (subCommand != null) {
            final List<String> subArgs = Arrays.asList(args).subList(1, args.length);
            CommandHelper helper = new CommandHelper(plugin);

            subCommand.execute(sender, subArgs.toArray(String[]::new), helper);
            return true;
        } else {
            sender.sendMessage("No Command for " + arg + " in credits");
        }
        return true;
    }

    private CreditSubCommand getCommand(String name) {
        return this.subCommands.stream()
                .filter(cmd -> cmd.getName().equals(name.toLowerCase()))
                .findFirst()
                .orElse(null);
    }
}
