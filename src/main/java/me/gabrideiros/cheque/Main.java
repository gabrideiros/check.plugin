package me.gabrideiros.cheque;

import me.gabrideiros.cheque.command.CheckCommand;
import me.gabrideiros.cheque.lib.listeners.InventoryListener;
import me.gabrideiros.cheque.manager.CheckManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private Economy economy;
    private CheckManager manager;

    @Override
    public void onEnable() {

       setupEconomy();

       new InventoryListener(this);

       manager = new CheckManager(this);

       getCommand("cheque").setExecutor(new CheckCommand(manager));
    }

    @Override
    public void onDisable() {}

    private boolean setupEconomy() {
        final RegisteredServiceProvider<Economy> economyProvider = (RegisteredServiceProvider<Economy>)this.getServer().getServicesManager().getRegistration((Class)Economy.class);
        if (economyProvider != null) {
            this.economy = (Economy)economyProvider.getProvider();
        }
        return this.economy != null;
    }

    public Economy getEconomy() {
        return economy;
    }
}
