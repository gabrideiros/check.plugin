package me.gabrideiros.cheque.listener;

import com.tke.cash.metodos.Cash;
import de.tr7zw.nbtapi.NBTItem;
import lombok.AllArgsConstructor;
import me.gabrideiros.cheque.Main;
import me.gabrideiros.cheque.lib.inventory.buttons.ItemButton;
import me.gabrideiros.cheque.manager.CheckManager;
import me.gabrideiros.cheque.util.NumberFormatter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

@AllArgsConstructor
public class CheckListener implements Listener {

    private final Main plugin;
    private final CheckManager checkManager;

    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        Player player = event.getPlayer();

        checkManager.getMoney().remove(player.getName());
        checkManager.getCash().remove(player.getName());

    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {

        Player player = event.getPlayer();
        String message = event.getMessage();

        if (checkManager.getMoney().contains(player.getName())) {

            event.setCancelled(true);

            if (message.equalsIgnoreCase("cancelar")) {
                player.sendMessage("§cAção cancelada com êxito!");
                checkManager.getMoney().remove(player.getName());
                return;
            }

            double value;

            NumberFormatter formatter = new NumberFormatter();

            try {
                value = formatter.parseString(message, false);
            } catch (Exception e) {
                player.sendMessage("§cOps! Este número é inválido");
                return;
            }

            if (value <= 1) {
                player.sendMessage("§cOps! Este número é inválido");
                return;
            }

            double amount = plugin.getEconomy().getBalance(player.getName());

            if (amount < value) {
                player.sendMessage("§cOps! Você não possui esta quantia.");
                checkManager.getMoney().remove(player.getName());
                return;
            }

            PlayerInventory inventory = player.getInventory();

            if (inventory.firstEmpty() == -1) {
                player.sendMessage(new String[]{
                        "§c§l  Ops!",
                        "§c  Seu inventário está cheio!",
                        ""
                });
                checkManager.getMoney().remove(player.getName());
                return;
            }

            ItemStack item = new ItemButton(
                    Material.PAPER,
                    "§2Cheque de Coins",
                    "§fValor: §2$" + formatter.formatNumber(value),
                    "",
                    "§7Clique com o direito para ativar!"
            ).getItem();

            NBTItem nbtItem = new NBTItem(item);
            nbtItem.setDouble("check_money", value);

            inventory.addItem(nbtItem.getItem());

            plugin.getEconomy().withdrawPlayer(player.getName(), value);
            player.sendMessage(new String[]{
                    "",
                    "§a§l  SUCESSO!",
                    "§f  Cheque com §2$§f" + formatter.formatNumber(value) + "§f coins criado.",
                    ""
            });

            checkManager.getMoney().remove(player.getName());
            return;
        }

        if (checkManager.getCash().contains(player.getName())) {

            event.setCancelled(true);

            if (message.equalsIgnoreCase("cancelar")) {
                player.sendMessage("§cAção cancelada com êxito!");
                checkManager.getCash().remove(player.getName());
                return;
            }

            double value;

            NumberFormatter formatter = new NumberFormatter();

            try {
                value = formatter.parseString(message, true);
            } catch (Exception e) {
                player.sendMessage("§cOps! Este número é inválido");
                return;
            }

            if (value <= 1) {
                player.sendMessage("§cOps! Este número é inválido");
                return;
            }

            double amount = Cash.get(player.getName());

            if (amount < value) {
                player.sendMessage("§cOps! Você não possui esta quantia.");
                checkManager.getCash().remove(player.getName());
                return;
            }

            PlayerInventory inventory = player.getInventory();

            if (inventory.firstEmpty() == -1) {
                player.sendMessage(new String[]{
                        "§c§l  Ops!",
                        "§c  Seu inventário está cheio!",
                        ""
                });
                checkManager.getCash().remove(player.getName());
                return;
            }

            ItemStack item = new ItemButton(
                    Material.GOLD_INGOT,
                    "§6Cheque de Cash",
                    "§fQuantia: §6✪" + formatter.formatNumber(value),
                    "",
                    "§7Clique com o direito para ativar!"
            ).getItem();

            NBTItem nbtItem = new NBTItem(item);
            nbtItem.setDouble("check_cash", value);

            inventory.addItem(nbtItem.getItem());

            Cash.remove(player.getName(), (int) value);
            player.sendMessage(new String[]{
                    "",
                    "§a§l  SUCESSO!",
                    "§f  Cheque com §6✪§f" + formatter.formatNumber(value) + "§f cash criado.",
                    ""
            });
            checkManager.getCash().remove(player.getName());
        }
    }

    @EventHandler
    public void onInteract(final PlayerInteractEvent event) {

        Player player = event.getPlayer();
        Action action = event.getAction();

        ItemStack item = event.getItem();

        NumberFormatter formatter = new NumberFormatter();

        if (item == null) return;

        if (!item.hasItemMeta()) return;

        if (!action.equals(Action.RIGHT_CLICK_AIR) && !action.equals(Action.RIGHT_CLICK_BLOCK)) return;

        if (checkManager.isCheckMoney(item)) {

            double value = checkManager.getCheckMoneyAmount(item);

            if (player.isSneaking()) {
                value = checkManager.getAllChecksMoney(player.getInventory());
                checkManager.removeMoneyChecks(player.getInventory());
            }

            plugin.getEconomy().depositPlayer(player.getName(), value);
            player.sendMessage("§a§lGG! §aVocê ativou cheque com §2$" + formatter.formatNumber(value) + "§a de coins.");

            if (item.getAmount() > 1) {
                item.setAmount(item.getAmount() - 1);
            }

            player.getInventory().remove(item);
            return;
        }

        if (checkManager.isCheckCash(item)) {

            double value = checkManager.getCheckCashAmount(item);

            if (player.isSneaking()) {
                value = checkManager.getAllChecksCash(player.getInventory());
                checkManager.removeCashChecks(player.getInventory());
            }

            Cash.add(player.getName(), (int) value);
            player.sendMessage("§a§lGG! §aVocê ativou cheque com §6✪" + formatter.formatNumber(value) + "§a de cash.");

            if (item.getAmount() > 1) {
                item.setAmount(item.getAmount() - 1);
            }

            player.getInventory().remove(item);
        }
    }
}
