package me.gabrideiros.cheque.command;

import me.gabrideiros.cheque.manager.CheckManager;
import me.gabrideiros.cheque.menu.CheckMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CheckCommand implements CommandExecutor {

    private final CheckMenu checkMenu;

    public CheckCommand(CheckMenu checkMenu) {
        this.checkMenu = checkMenu;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;

        checkMenu.open(player);

        return false;
    }
}
