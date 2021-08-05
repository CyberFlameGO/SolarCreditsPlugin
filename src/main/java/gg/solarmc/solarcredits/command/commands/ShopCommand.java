package gg.solarmc.solarcredits.command.commands;

import gg.solarmc.solarcredits.SolarCredit;
import gg.solarmc.solarcredits.command.CommandHelper;
import gg.solarmc.solarcredits.command.CreditSubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public record ShopCommand(SolarCredit plugin) implements CreditSubCommand {
    @Override
    public void execute(CommandSender sender, String[] args, CommandHelper helper) {
        if (sender instanceof Player player)
            plugin.getShop().openShop(player);
        else
            sender.sendMessage(ChatColor.RED + "Only Players use this Command");
    }

    @Override
    public String getName() {
        return "shop";
    }

    @Override
    public String getArgs() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Opens the credits shop for the day";
    }

    @Override
    public String getPermission() {
        return null;
    }
}
