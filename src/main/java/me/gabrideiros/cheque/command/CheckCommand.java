package me.gabrideiros.cheque.command;

import me.gabrideiros.cheque.manager.CheckManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CheckCommand implements CommandExecutor {

    private CheckManager manager;

    public CheckCommand(CheckManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;

        manager.open(player);

        return false;
    }
}
