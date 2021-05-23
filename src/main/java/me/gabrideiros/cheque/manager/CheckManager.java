package me.gabrideiros.cheque.manager;

import de.tr7zw.nbtapi.NBTItem;
import lombok.Getter;
import me.gabrideiros.cheque.Main;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Getter
public class CheckManager {

    private final Main plugin;

    private final List<String> money;
    private final List<String> cash;

    public CheckManager(Main plugin) {
        this.plugin = plugin;
        this.money = new ArrayList<>();
        this.cash = new ArrayList<>();
    }

    public boolean isCheckMoney(ItemStack item) {
        return new NBTItem(item).hasKey("check_money");
    }

    public boolean isCheckCash(ItemStack item) {
        return new NBTItem(item).hasKey("check_cash");
    }

    public double getCheckMoneyAmount(ItemStack item) {
        return new NBTItem(item).getDouble("check_money") * item.getAmount();
    }

    public double getCheckCashAmount(ItemStack item) {
        return new NBTItem(item).getDouble("check_cash");
    }

    public double getAllChecksMoney(Inventory inventory) {
        return Arrays.stream(inventory.getContents())
                .filter(Objects::nonNull)
                .filter(this::isCheckMoney)
                .mapToDouble(this::getCheckMoneyAmount)
                .sum();
    }

    public double getAllChecksCash(Inventory inventory) {
        return Arrays.stream(inventory.getContents())
                .filter(Objects::nonNull)
                .filter(this::isCheckCash)
                .mapToDouble(this::getCheckCashAmount)
                .sum();
    }

    public void removeMoneyChecks(Inventory inventory) {
        Stream<ItemStack> items =  Arrays.stream(inventory.getContents())
                .filter(Objects::nonNull)
                .filter(this::isCheckMoney);

        items.forEach(inventory::remove);
    }

    public void removeCashChecks(Inventory inventory) {
        Stream<ItemStack> items =  Arrays.stream(inventory.getContents())
                .filter(Objects::nonNull)
                .filter(this::isCheckCash);

        items.forEach(inventory::remove);
    }
}
