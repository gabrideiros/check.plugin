package me.gabrideiros.cheque.menu;

import lombok.AllArgsConstructor;
import me.gabrideiros.cheque.lib.inventory.buttons.ItemButton;
import me.gabrideiros.cheque.lib.inventory.menus.InventoryGUI;
import me.gabrideiros.cheque.lib.inventory.utils.InventorySize;
import me.gabrideiros.cheque.manager.CheckManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class CheckMenu {

    private final CheckManager checkManager;

    public void open(Player player) {

        InventoryGUI gui = new InventoryGUI(
                "Criar cheque",
                InventorySize.THREE_ROWS
        );

        gui.setDefaultAllCancell(true);
        gui.setButton(12, new ItemButton(
                Material.EMERALD,
                "§2Cheque de Coins",
                "§7Transforme os seus coins",
                "§7em cheques para trocar",
                "§7com os jogadores."
        ).setDefaultAction(e -> {

            player.closeInventory();

            if (checkManager.getMoney().contains(player.getName())) {
                player.sendMessage("§cOps! Você já possui uma criação pendente.");
                return;
            }

            player.sendMessage(new String[]{
                    "",
                    "§a§l CHEQUE DE COINS!",
                    "§f  Digite no chat a quantia que você deseja",
                    "§f  transformar em cheque de coins.",
                    "",
                    "§7  ➟ Digite §ncancelar§7 caso deseje parar a ação.",
                    ""
            });

            checkManager.getMoney().add(player.getName());

        }));

        gui.setButton(14, new ItemButton(
                Material.GOLD_INGOT,
                "§6Cheque de Cash",
                "§7Transforme os seus cash",
                "§7em cheques para trocar",
                "§7com os jogadores."
        ).setDefaultAction(e -> {

            player.closeInventory();

            if (checkManager.getCash().contains(player.getName())) {
                player.sendMessage("§cOps! Você já possui uma criação pendente.");
                return;
            }

            player.sendMessage(new String[]{
                    "",
                    "§a§l CHEQUE DE CASH!",
                    "§f  Digite no chat a quantia que você deseja",
                    "§f  transformar em cheque de cash.",
                    "",
                    "§7  ➟ Digite §ncancelar§7 caso deseje parar a ação.",
                    ""
            });

            checkManager.getCash().add(player.getName());

        }));

        gui.show(player);
    }
}
