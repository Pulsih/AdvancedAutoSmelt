package me.pulsi_.advancedautosmelt.events;

import me.pulsi_.advancedautosmelt.managers.DataManager;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ChestBreak implements Listener {

    private final List<String> worldsBlackList;
    private final boolean useLegacySupp;
    public ChestBreak(DataManager dm) {
        this.useLegacySupp = dm.isUseLegacySupp();
        this.worldsBlackList = dm.getWorldsBlackList();
    }

    private final ItemStack chest = new ItemStack(Material.CHEST, 1);

    public void removeDrops(BlockBreakEvent e) {
        if (useLegacySupp) {
            e.getBlock().setType(Material.AIR);
        } else {
            e.setDropItems(false);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void chestBreak(BlockBreakEvent e) {

        Player p = e.getPlayer();

        for (String disabledWorlds : worldsBlackList)
            if (disabledWorlds.contains(p.getWorld().getName())) return;

        if (e.isCancelled()) return;
        if (!(e.getBlock().getType() == Material.CHEST)) return;
        p.getInventory().addItem(chest);
        if (e.getBlock().getState() instanceof Chest || e.getBlock().getState() instanceof DoubleChest) {
            Chest chestBlock = ((Chest) e.getBlock().getState());
            for (ItemStack itemsInTheChest : chestBlock.getInventory().getContents()) {
                if (itemsInTheChest != null) {
                    p.getLocation().getWorld().dropItem(p.getLocation(), itemsInTheChest).setPickupDelay(0);
                    itemsInTheChest.setAmount(0);
                }
            }
            removeDrops(e);
        }
    }
}