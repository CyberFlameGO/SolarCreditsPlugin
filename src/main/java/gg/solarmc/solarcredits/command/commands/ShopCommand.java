package gg.solarmc.solarcredits.command.commands;

import gg.solarmc.solarcredits.SolarCredit;
import gg.solarmc.solarcredits.command.CommandHelper;
import gg.solarmc.solarcredits.command.CreditSubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public record ShopCommand(SolarCredit plugin) implements CreditSubCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args, CommandHelper helper) {
        if (sender instanceof Player player)
            plugin.getShop().openShop(player);
        else
            sender.sendMessage("Only Players use this Command");

        return true;
    }

    @Override
    public String getName() {
        return "shop";
    }
}
