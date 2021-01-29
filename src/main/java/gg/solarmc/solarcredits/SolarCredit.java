package gg.solarmc.solarcredits;

import org.bukkit.plugin.java.JavaPlugin;

public class SolarCredit extends JavaPlugin {
	
	@Override
	public void onEnable() {
		getCommand("credits").setExecutor(new CreditCommands(this));
	}
	
	@Override
	public void onDisable() {
		
	}

}
