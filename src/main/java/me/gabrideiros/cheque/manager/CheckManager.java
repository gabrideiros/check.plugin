package me.gabrideiros.cheque.manager;

import com.tke.cash.metodos.Cash;
import me.gabrideiros.cheque.Main;
import me.gabrideiros.cheque.lib.buttons.ItemButton;
import me.gabrideiros.cheque.lib.menus.InventoryGUI;
import me.gabrideiros.cheque.lib.utils.InventorySize;
import me.gabrideiros.cheque.util.NumberFormatter;
import org.bukkit.Bukkit;
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

import java.util.ArrayList;
import java.util.List;

public class CheckManager implements Listener {

    private Main plugin;

    private List<String> money;
    private List<String> cash;

    public CheckManager(Main plugin) {
        this.plugin = plugin;
        this.money = new ArrayList<>();
        this.cash = new ArrayList<>();

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void open(Player player) {

        InventoryGUI gui = new InventoryGUI(
                "Criar cheque",
                InventorySize.THREE_ROWS
        );

        gui.setDefaultAllCancell(true);
        gui.setButton(12, new ItemButton(
                Material.EMERALD,
                "§aCheque de dinheiro",
                "§7Clique para criar um cheque de",
                "§7dinheiro."
        ).setDefaultAction(e -> {

            player.closeInventory();
            player.sendMessage(new String[]{
                    "§aDigite no chat a quantia de dinheiro",
                    "§aque deseja transformar em cheque:",
                    "§7Caso queira cancelar digite 'cancelar'."
            });

            money.add(player.getName());

        }));

        gui.setButton(14, new ItemButton(
                Material.GOLD_INGOT,
                "§aCheque de gold",
                "§7Clique para criar um cheque de",
                "§7gold."
        ).setDefaultAction(e -> {

            player.closeInventory();
            player.sendMessage(new String[]{
                    "§aDigite no chat a quantia de gold",
                    "§aque deseja transformar em cheque:",
                    "§7Caso queira cancelar digite 'cancelar'."
            });

            cash.add(player.getName());

        }));

        gui.show(player);
    }


    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (money.contains(player.getName())) money.remove(player.getName());

        if (cash.contains(player.getName())) cash.remove(player.getName());

    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {

        Player player = event.getPlayer();
        String message = event.getMessage();

        if (money.contains(player.getName())) {

            event.setCancelled(true);

            if (message.equalsIgnoreCase("cancelar")) {
                player.sendMessage("§cAção cancelada com sucesso!");
                money.remove(player.getName());
                return;
            }

            double value = 0;

            NumberFormatter formatter = new NumberFormatter();

            try {
                value = formatter.parseString(message);
            } catch (Exception e) {
                player.sendMessage("§cDigite um valor válido!");
                return;
            }

            if (value <= 0) {
                player.sendMessage("§cDigite um válor maior que zero!");
                return;
            }

            double amount = plugin.getEconomy().getBalance(player.getName());

            if (amount < value) {
                player.sendMessage("§cVocê não possui esta quantia!");
                cash.remove(player.getName());
                return;
            }

            PlayerInventory inventory = player.getInventory();

            if (inventory.firstEmpty() == -1) {
                player.sendMessage("§cSeu inventário está cheio!");
                cash.remove(player.getName());
                return;
            }

            ItemStack item = new ItemButton(
                    Material.PAPER,
                    "§aCheque de dinheiro",
                    "§fValor: §7" + formatter.formatNumber(value),
                    "",
                    "§aClique com o direito para ativar!"
            ).getItem();

            inventory.addItem(item);

            plugin.getEconomy().withdrawPlayer(player.getName(), value);
            player.sendMessage("§aVocê criou um cheque de dinheiro com §f" + formatter.formatNumber(value) + "§a.");

            money.remove(player.getName());
        }

        if (cash.contains(player.getName())) {

            event.setCancelled(true);

            if (message.equalsIgnoreCase("cancelar")) {
                player.sendMessage("§cAção cancelada com sucesso!");
                cash.remove(player.getName());
                return;
            }

            double value = 0;

            NumberFormatter formatter = new NumberFormatter();

            try {
                value = formatter.parseString(message);
            } catch (Exception e) {
                player.sendMessage("§cDigite um valor válido!");
                return;
            }

            if (value <= 0) {
                player.sendMessage("§cDigite um válor maior que zero!");
                return;
            }

            double amount = Cash.get(player.getName());

            if (amount < value) {
                player.sendMessage("§cVocê não possui esta quantia!");
                cash.remove(player.getName());
                return;
            }

            PlayerInventory inventory = player.getInventory();

            if (inventory.firstEmpty() == -1) {
                player.sendMessage("§cSeu inventário está cheio!");
                cash.remove(player.getName());
                return;
            }

            ItemStack item = new ItemButton(
                    Material.PAPER,
                    "§aCheque de gold",
                    "§fValor: §7" + formatter.formatNumber(value),
                    "",
                    "§aClique com o direito para ativar!"
            ).getItem();

            inventory.addItem(item);

            Cash.remove(player.getName(), (int) value);
            player.sendMessage("§aVocê criou um cheque de gold com §f" + formatter.formatNumber(value) + "§a.");

            cash.remove(player.getName());
        }
    }

    @EventHandler
    public void onInteract(final PlayerInteractEvent event) throws Exception {

        Player player = event.getPlayer();
        Action action = event.getAction();

        ItemStack item = player.getItemInHand();

        NumberFormatter formatter = new NumberFormatter();

        if (item == null) return;

        if (!item.getType().equals(Material.PAPER)) return;

        if (!item.hasItemMeta()) return;

        if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {

            String lore = item.getItemMeta().getLore().stream().filter(line -> line.contains("§fValor: §7")).findFirst().orElse(null);

            if (lore == null) return;

            double value = formatter.parseString(lore.replace("§fValor: §7", ""));

            switch (item.getItemMeta().getDisplayName()) {
                case "§aCheque de dinheiro":
                    plugin.getEconomy().depositPlayer(player.getName(), value);
                    player.sendMessage("§aCheque de dinheiro com §f" + formatter.formatNumber(value) + "§a ativado com sucesso!");
                    break;
                case "§aCheque de gold":
                    Cash.add(player.getName(), (int) value);
                    player.sendMessage("§aCheque de gold com §f" + formatter.formatNumber(value) + "§a ativado com sucesso!");
                    break;
                default:
                    break;
            }

            if (item.getAmount() > 1) {
                item.setAmount(item.getAmount() - 1);
                return;
            }
            player.getInventory().removeItem(new ItemStack[]{item});
        }
    }
}
