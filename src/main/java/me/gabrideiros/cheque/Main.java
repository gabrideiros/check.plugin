package me.gabrideiros.cheque;

import me.gabrideiros.cheque.command.CheckCommand;
import me.gabrideiros.cheque.lib.inventory.listeners.InventoryListener;
import me.gabrideiros.cheque.listener.CheckListener;
import me.gabrideiros.cheque.manager.CheckManager;
import me.gabrideiros.cheque.menu.CheckMenu;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private Economy economy;

    @Override
    public void onEnable() {

        setupEconomy();

        new InventoryListener(this);

        CheckManager manager = new CheckManager(this);

        CheckMenu checkMenu = new CheckMenu(manager);

        Bukkit.getPluginManager().registerEvents(new CheckListener(this, manager), this);

        getCommand("cheque").setExecutor(new CheckCommand(checkMenu));
    }

    @Override
    public void onDisable() {}

    private void setupEconomy() {
        final RegisteredServiceProvider<Economy> economyProvider = (RegisteredServiceProvider<Economy>)this.getServer().getServicesManager().getRegistration((Class<Economy>)Economy.class);
        if (economyProvider != null) {
            this.economy = (Economy)economyProvider.getProvider();
        }
    }

    public Economy getEconomy() {
        return economy;
    }
}
