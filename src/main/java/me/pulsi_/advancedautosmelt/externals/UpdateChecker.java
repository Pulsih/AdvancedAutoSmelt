package me.pulsi_.advancedautosmelt.externals;

import me.pulsi_.advancedautosmelt.AdvancedAutoSmelt;
import me.pulsi_.advancedautosmelt.utils.ChatUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class UpdateChecker implements Listener {

    private final boolean isUpToDate;
    private final AdvancedAutoSmelt plugin;
    public UpdateChecker(AdvancedAutoSmelt plugin) {
        boolean isUpdated;
        this.plugin = plugin;
        try {
            isUpdated = isPluginUpdated();
        } catch (IOException e) {
            isUpdated = true;
        }
        this.isUpToDate = isUpdated;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (!plugin.getConfig().getBoolean("Update-Checker") | !e.getPlayer().isOp() || !e.getPlayer().hasPermission("advancedautosmelt.notify") | isUpToDate) return;
        final TextComponent update = new TextComponent(ChatUtils.color("&8&l<&d&lAdvanced&a&lAuto&c&lSmelt&8&l> &aNew update available! &7(CLICK HERE)"));
        update.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/%E2%9C%A8-advancedautosmelt-%E2%9C%A8-autosmelt-autopickup-inventoryfull-alert-1-7-1-16-compatible.90587/"));
        update.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click here to download it!").color(ChatColor.WHITE).create()));
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            e.getPlayer().sendMessage("");
            e.getPlayer().spigot().sendMessage(update);
            e.getPlayer().sendMessage("");
        }, 80);
    }

    private boolean isPluginUpdated() throws IOException {
        final String currentVersion = new BufferedReader(new InputStreamReader(new URL("https://api.spigotmc.org/legacy/update.php?resource=90587").openConnection().getInputStream())).readLine();
        return plugin.getDescription().getVersion().equals(currentVersion);
    }
}