package me.pulsi_.advancedautosmelt.events;

import me.pulsi_.advancedautosmelt.commands.Commands;
import me.pulsi_.advancedautosmelt.managers.DataManager;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;
import java.util.Set;

public class FortuneSupport implements Listener {

    private final boolean isEFS;
    private final boolean useWhitelist;
    private final boolean isDCM;
    private final boolean isSmeltGold;
    private final boolean isSmeltIron;
    private final boolean isSmeltStone;
    private final boolean isAutoPickupEnabled;
    private final boolean useLegacySupp;
    private final List<String> whitelist;
    private final List<String> disabledWorlds;

    public FortuneSupport(DataManager dm) {
        this.isEFS = dm.isEFS();
        this.useWhitelist = dm.useWhitelist();
        this.isDCM = dm.isDCM();
        this.isSmeltGold = dm.isSmeltGold();
        this.isSmeltIron =  dm.isSmeltIron();
        this.isSmeltStone = dm.isSmeltStone();
        this.isAutoPickupEnabled = dm.isAutoPickupEnabled();
        this.whitelist = dm.getWhiteList();
        this.disabledWorlds = dm.getWorldsBlackList();
        this.useLegacySupp = dm.isUseLegacySupp();
    }

    private final Set<String> autoPickupOFF = Commands.autoPickupOFF;
    private final Set<String> autoSmeltOFF = Commands.autoSmeltOFF;

    private final ItemStack goldIngot = new ItemStack(Material.GOLD_INGOT);
    private final ItemStack goldOre = new ItemStack(Material.GOLD_ORE);
    private final ItemStack ironIngot = new ItemStack(Material.IRON_INGOT);
    private final ItemStack ironOre = new ItemStack(Material.IRON_ORE);
    private final ItemStack stone = new ItemStack(Material.STONE);
    private final ItemStack cobblestone = new ItemStack(Material.COBBLESTONE);

    public void removeDrops(BlockBreakEvent e) {
        if (useLegacySupp) {
            e.getBlock().setType(Material.AIR);
        } else {
            e.setDropItems(false);
        }
    }

    public void smelt(Player p, ItemStack smelt, ItemStack notSmelt, BlockBreakEvent e) {

        boolean cantPickup = autoPickupOFF.contains(p.getName());
        boolean cantSmelt = autoSmeltOFF.contains(p.getName());

        if (!cantPickup && !cantSmelt) {
            if (!p.getInventory().addItem(smelt).isEmpty()) {
                p.getWorld().dropItem(p.getLocation(), smelt);
            }

        } else if (cantPickup && cantSmelt) {
            return;

        } else if (cantPickup) {
            e.getBlock().getWorld().dropItem(e.getBlock().getLocation(), smelt);

        } else if (cantSmelt) {
            if (!p.getInventory().addItem(notSmelt).isEmpty()) {
                p.getWorld().dropItem(p.getLocation(), notSmelt);
            }
        }
    }

    public void smeltNoPickup(Player p, ItemStack smelt, BlockBreakEvent e) {
        if (!Commands.autoSmeltOFF.contains(p.getName())) {
            e.getBlock().getWorld().dropItem(e.getBlock().getLocation(), smelt);
        }
    }

    public void pickNoSmelt(Player p, ItemStack noSmelt) {
        if (!autoPickupOFF.contains(p.getName())) {
            if (!p.getInventory().addItem(noSmelt).isEmpty()) {
                p.getWorld().dropItem(p.getLocation(), noSmelt);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreakFortune(BlockBreakEvent e) {

        Player p = e.getPlayer();

        for (String disabledWorlds : disabledWorlds)
            if (disabledWorlds.contains(p.getWorld().getName())) return;

        if (e.isCancelled()) return;
        if (!isEFS) return;
        if (e.getBlock().getType() == Material.STONE || e.getBlock().getType() == Material.IRON_ORE
                || e.getBlock().getType() == Material.GOLD_ORE || e.getBlock().getType() == Material.CHEST
                || e.getBlock().getType() == Material.FURNACE || e.getBlock().getType() == Material.ENDER_CHEST) return;
        if (!(p.hasPermission("advancedautosmelt.fortune"))) return;
        if (isDCM) {if (p.getGameMode().equals(GameMode.CREATIVE)) return;}

        if (useWhitelist) {
            if (whitelist.contains(e.getBlock().getType().toString())) {

                if (p.getInventory().getItemInHand().containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS)) {
                    int fortuneLevel = p.getInventory().getItemInHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);

                    Random r = new Random();
                    int min = 1;
                    int multiply = r.nextInt((fortuneLevel - min) + 1) + min;
                    for (ItemStack drops : e.getBlock().getDrops()) {
                        drops.setAmount(multiply);

                        if (isAutoPickupEnabled) {
                            if (!autoPickupOFF.contains(p.getName())) {
                                if (!p.getInventory().addItem(drops).isEmpty()) {
                                    p.getWorld().dropItem(p.getLocation(), drops);
                                }
                            } else {
                                e.getBlock().getLocation().getWorld().dropItem(p.getLocation(), drops);
                            }
                        } else {
                            e.getBlock().getLocation().getWorld().dropItem(p.getLocation(), drops);
                        }
                    }

                } else {

                    for (ItemStack drops : e.getBlock().getDrops()) {

                        if (isAutoPickupEnabled) {
                            if (!autoPickupOFF.contains(p.getName())) {
                                if (!p.getInventory().addItem(drops).isEmpty()) {
                                    p.getWorld().dropItem(p.getLocation(), drops);
                                }
                            } else {
                                e.getBlock().getLocation().getWorld().dropItem(p.getLocation(), drops);
                            }
                        } else {
                            e.getBlock().getLocation().getWorld().dropItem(p.getLocation(), drops);
                        }
                    }
                }
            } else {
                for (ItemStack drops : e.getBlock().getDrops()) {

                    if (isAutoPickupEnabled) {
                        if (!autoPickupOFF.contains(p.getName())) {
                            if (!p.getInventory().addItem(drops).isEmpty()) {
                                p.getWorld().dropItem(p.getLocation(), drops);
                            }
                        } else {
                            e.getBlock().getLocation().getWorld().dropItem(p.getLocation(), drops);
                        }
                    } else {
                        e.getBlock().getLocation().getWorld().dropItem(p.getLocation(), drops);
                    }
                }
            }

        } else {

            if (p.getInventory().getItemInHand().containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS)) {
                int fortuneLevel = p.getInventory().getItemInHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);

                Random r = new Random();
                int min = 1;
                int multiply = r.nextInt((fortuneLevel - min) + 1) + min;
                for (ItemStack drops : e.getBlock().getDrops()) {
                    drops.setAmount(multiply);

                    if (isAutoPickupEnabled) {
                        if (!autoPickupOFF.contains(p.getName())) {
                            if (!p.getInventory().addItem(drops).isEmpty()) {
                                p.getWorld().dropItem(p.getLocation(), drops);
                            }
                        } else {
                            e.getBlock().getLocation().getWorld().dropItem(p.getLocation(), drops);
                        }
                    } else {
                        e.getBlock().getLocation().getWorld().dropItem(p.getLocation(), drops);
                    }
                }
            } else {
                for (ItemStack drops : e.getBlock().getDrops()) {

                    if (isAutoPickupEnabled) {
                        if (!autoPickupOFF.contains(p.getName())) {
                            if (!p.getInventory().addItem(drops).isEmpty()) {
                                p.getWorld().dropItem(p.getLocation(), drops);
                            }
                        } else {
                            e.getBlock().getLocation().getWorld().dropItem(p.getLocation(), drops);
                        }
                    } else {
                        e.getBlock().getLocation().getWorld().dropItem(p.getLocation(), drops);
                    }
                }
            }
        }
        removeDrops(e);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onStoneBreakFortune(BlockBreakEvent e) {

        Player p = e.getPlayer();

        for (String disabledWorlds : disabledWorlds)
            if (disabledWorlds.contains(p.getWorld().getName())) return;

        if (e.isCancelled()) return;
        if (!isEFS) return;
        if (!(e.getBlock().getType() == Material.STONE)) return;
        if (!(p.hasPermission("advancedautosmelt.fortune"))) return;
        if (isDCM) {if (p.getGameMode().equals(GameMode.CREATIVE)) return;}

        if (useWhitelist) {
            if (whitelist.contains(Material.STONE.toString())) {

                if (p.getInventory().getItemInHand().containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS)) {
                    int fortuneLevel = p.getInventory().getItemInHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);

                    Random r = new Random();
                    int min = 1;
                    int multiply = r.nextInt((fortuneLevel - min) + 1) + min;
                    stone.setAmount(multiply);
                    cobblestone.setAmount(multiply);

                } else {
                    stone.setAmount(1);
                    cobblestone.setAmount(1);
                }
            }

        } else {

            if (p.getInventory().getItemInHand().containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS)) {
                int fortuneLevel = p.getInventory().getItemInHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);

                Random r = new Random();
                int min = 1;
                int multiply = r.nextInt((fortuneLevel - min) + 1) + min;

                stone.setAmount(multiply);
                cobblestone.setAmount(multiply);
            } else {
                stone.setAmount(1);
                cobblestone.setAmount(1);
            }

        }
        if (isSmeltStone) {
            if (isAutoPickupEnabled) {
                if (!autoPickupOFF.contains(p.getName())) {
                    if (!autoSmeltOFF.contains(p.getName())) {
                        smelt(p, stone, cobblestone, e);
                    } else {
                        if (autoPickupOFF.contains(p.getName())) return;
                        pickNoSmelt(p, cobblestone);
                    }
                } else {
                    if (autoSmeltOFF.contains(p.getName())) return;
                    smeltNoPickup(p, stone, e);
                }
            } else {
                if (autoSmeltOFF.contains(p.getName())) return;
                smeltNoPickup(p, stone, e);
            }
        } else {
            if (!isAutoPickupEnabled) return;
            if (autoPickupOFF.contains(p.getName())) return;
            pickNoSmelt(p, cobblestone);
        }
        removeDrops(e);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onIronOreBreakFortune(BlockBreakEvent e) {

        Player p = e.getPlayer();

        for (String disabledWorlds : disabledWorlds)
            if (disabledWorlds.contains(p.getWorld().getName())) return;

        if (e.isCancelled()) return;
        if (!isEFS) return;
        if (!(e.getBlock().getType() == Material.IRON_ORE)) return;
        if (!(p.hasPermission("advancedautosmelt.fortune"))) return;
        if (isDCM) {if (p.getGameMode().equals(GameMode.CREATIVE)) return;}

        if (useWhitelist) {
            if (whitelist.contains(Material.IRON_ORE.toString())) {

                if (p.getInventory().getItemInHand().containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS)) {
                    int fortuneLevel = p.getInventory().getItemInHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);

                    Random r = new Random();
                    int min = 1;
                    int randomDrop = r.nextInt((fortuneLevel - min) + 1) + min;

                    ironIngot.setAmount(randomDrop);
                    ironOre.setAmount(randomDrop);

                } else {
                    ironIngot.setAmount(1);
                    ironOre.setAmount(1);
                }
            }

        } else {

            if (p.getInventory().getItemInHand().containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS)) {
                int fortuneLevel = p.getInventory().getItemInHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);

                Random r = new Random();
                int min = 1;
                int randomDrop = r.nextInt((fortuneLevel - min) + 1) + min;
                ironIngot.setAmount(randomDrop);
                ironOre.setAmount(randomDrop);

            } else {
                ironIngot.setAmount(1);
                ironOre.setAmount(1);
            }

        }
        if (isSmeltIron) {
            if (isAutoPickupEnabled) {
                if (!autoPickupOFF.contains(p.getName())) {
                    if (!autoSmeltOFF.contains(p.getName())) {
                        smelt(p, ironIngot, ironOre, e);
                    } else {
                        if (autoPickupOFF.contains(p.getName())) return;
                        pickNoSmelt(p, ironOre);
                    }
                } else {
                    if (autoSmeltOFF.contains(p.getName())) return;
                    smeltNoPickup(p, ironIngot, e);
                }
            } else {
                if (autoSmeltOFF.contains(p.getName())) return;
                smeltNoPickup(p, ironIngot, e);
            }
        } else {
            if (!isAutoPickupEnabled) return;
            if (autoPickupOFF.contains(p.getName())) return;
            pickNoSmelt(p, ironOre);
        }
        removeDrops(e);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onGoldOreBreakFortune(BlockBreakEvent e) {

        Player p = e.getPlayer();

        for (String disabledWorlds : disabledWorlds)
            if (disabledWorlds.contains(p.getWorld().getName())) return;

        if (e.isCancelled()) return;
        if (!isEFS) return;
        if (!(e.getBlock().getType() == Material.GOLD_ORE)) return;
        if (!(p.hasPermission("advancedautosmelt.fortune"))) return;
        if (isDCM) {if (p.getGameMode().equals(GameMode.CREATIVE)) return;}

        if (useWhitelist) {
            if (whitelist.contains(Material.GOLD_ORE.toString())) {

                if (p.getInventory().getItemInHand().containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS)) {
                    int fortuneLevel = p.getInventory().getItemInHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);

                    Random r = new Random();
                    int min = 1;
                    int multiply = r.nextInt((fortuneLevel - min) + 1) + min;

                    goldIngot.setAmount(multiply);
                    goldOre.setAmount(multiply);
                } else {
                    goldIngot.setAmount(1);
                    goldOre.setAmount(1);
                }
            }

        } else {

            if (p.getInventory().getItemInHand().containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS)) {
                int fortuneLevel = p.getInventory().getItemInHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);

                Random r = new Random();
                int min = 1;
                int multiply = r.nextInt((fortuneLevel - min) + 1) + min;

                goldIngot.setAmount(multiply);
                goldOre.setAmount(multiply);
            } else {
                goldIngot.setAmount(1);
                goldOre.setAmount(1);
            }

        }
        if (isSmeltGold) {
            if (isAutoPickupEnabled) {
                if (!autoPickupOFF.contains(p.getName())) {
                    if (!autoSmeltOFF.contains(p.getName())) {
                        smelt(p, goldIngot, goldOre, e);
                    } else {
                        if (autoPickupOFF.contains(p.getName())) return;
                        pickNoSmelt(p, goldOre);
                    }
                } else {
                    if (autoSmeltOFF.contains(p.getName())) return;
                    smeltNoPickup(p, goldIngot, e);
                }
            } else {
                if (autoSmeltOFF.contains(p.getName())) return;
                smeltNoPickup(p, goldIngot, e);
            }
        } else {
            if (!isAutoPickupEnabled) return;
            if (autoPickupOFF.contains(p.getName())) return;
            pickNoSmelt(p, goldOre);
        }
        removeDrops(e);
    }
}