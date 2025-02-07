package top.heartstring.teachat.chat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import top.heartstring.teachat.utils.BukkitMessage;
import top.heartstring.teachat.utils.ColorUtil;

public class ChatModule {
    private final String originalKey;
    protected String condition;
    protected final boolean isPerm;
    protected final Map<String, String> msg = new HashMap<>();
    protected final Map<String, String> click = new HashMap<>();
    protected final Map<String, List<String>> hover = new HashMap<>();

    public ChatModule(String condition) {
        this.condition = condition;
        this.originalKey = condition;
        if (condition.contains("{}")) {
            this.condition = condition.replace("{}", ".").replace("%", "");
            this.isPerm = true;
        } else {
            this.isPerm = false;
        }

    }

    public String msg(OfflinePlayer player, String s, boolean hexColor) {
        String Msg = this.getModuleMsg(player, this.msg);
        Msg = Msg != null ? Msg.replaceAll("%default_msg%", s) : "undefined";
        return hexColor ? ColorUtil.parseColor(PlaceholderAPI.setPlaceholders(player, Msg)) : PlaceholderAPI.setPlaceholders(player, Msg);
    }

    public BaseComponent component(OfflinePlayer player, String s, boolean hexColor) {
        String Msg = this.msg(player, s, hexColor);


        return new TextComponent(Msg);
    }

    public void hover(OfflinePlayer player, BaseComponent msg, boolean hexColor) {
        String value;
        if (this.isPerm && player.isOnline()) {
            value = player.getPlayer().hasPermission(this.condition) ? "action" : "default";
        } else {
            value = PlaceholderAPI.setPlaceholders(player, this.condition);
        }

        List<String> list = this.hover.get(value);
        if (list == null) {
            list = this.hover.get("default");
            if (list == null) {
                return;
            }
        }

        TextComponent[] texts = new TextComponent[list.size()];
        int i;
        if (hexColor) {
            for(i = 0; i < list.size(); ++i) {
                texts[i] = new TextComponent(ColorUtil.parseColor(PlaceholderAPI.setPlaceholders(player, list.get(i))));
            }
        } else {
            for(i = 0; i < list.size(); ++i) {
                texts[i] = new TextComponent(PlaceholderAPI.setPlaceholders(player, list.get(i)));
            }
        }

        msg.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, texts));
    }

    public void click(OfflinePlayer player, BaseComponent msg) {
        String Msg = this.getModuleMsg(player, this.click);
        msg.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.SUGGEST_COMMAND, Msg != null ? Msg.replaceAll("\\[", "").replaceAll("]", "") : "undefined"));
    }

    public String getModuleMsg(OfflinePlayer player, Map<String, String> list) {
        String Msg;
        if (this.isPerm && player.isOnline()) {
            Msg = list.get(player.getPlayer().hasPermission(this.condition) ? "action" : "default");
        } else {
            Msg = list.get(PlaceholderAPI.setPlaceholders(player, this.condition));
        }

        if (Msg == null) {
            Msg = list.get("default");
        }

        return Msg;
    }

    public void sectionBus(Map<String, ConfigurationSection> sectionMap) {
        ConfigurationSection moduleSection = sectionMap.get(this.originalKey);
        if (moduleSection != null) {
            this.msg.clear();
            this.hover.clear();
            this.click.clear();
            if (!this.isPerm) {
                this.condition = "%" + this.originalKey + "%";
            } else {
                this.condition = this.originalKey.replace("{}", ".");
            }

            moduleSection.getKeys(false).forEach((key) -> {
                this.msg.put(key, moduleSection.getString(key + ".msg", "undefined"));
                this.hover.put(key, moduleSection.getStringList(key + ".hover"));
                this.click.put(key, moduleSection.getString(key + ".click", "undefined"));
            });
        }

    }
}
