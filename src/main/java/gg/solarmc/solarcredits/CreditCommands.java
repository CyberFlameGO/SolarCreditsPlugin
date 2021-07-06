package gg.solarmc.solarcredits;

import gg.solarmc.solarcredits.command.CommandHelper;
import gg.solarmc.solarcredits.command.CreditSubCommand;
import gg.solarmc.solarcredits.command.commands.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CreditCommands implements CommandExecutor {
    private final SolarCredit plugin;
    private final List<CreditSubCommand> subCommands;
    private final CommandHelper helper;

    public CreditCommands(SolarCredit plugin, CommandHelper helper, String tebexSecret) {
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
        this.helper = helper;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            String commands = "Solar Credits Commands : \n" +
                    subCommands.stream()
                            .map(CreditSubCommand::getName)
                            .collect(Collectors.joining("\n")) +
                    "\n/credits <command>";
            // TODO: make it look good
            sender.sendMessage(commands.split("\n"));
            return true;
        }

        String arg = args[0];
        CreditSubCommand subCommand = getCommand(arg);

        if (subCommand != null) {
            List<String> subArgs = Arrays.asList(args).subList(1, args.length);

            subCommand.execute(sender, subArgs.toArray(String[]::new), this.helper);
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
