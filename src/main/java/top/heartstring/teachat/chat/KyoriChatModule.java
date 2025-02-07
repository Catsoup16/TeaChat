package top.heartstring.teachat.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.event.ClickEvent.Action;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import top.heartstring.teachat.BukkitCore;
import top.heartstring.teachat.network.packet.PlayerChatPacket;
import top.heartstring.teachat.utils.BukkitMessage;
import top.heartstring.teachat.utils.ColorUtil;
import top.heartstring.teachat.utils.LoggerUtil;
import top.heartstring.teachat.utils.PaperUtil;

public class KyoriChatModule extends ChatModule {
    private static final GsonComponentSerializer serializer2Gson = GsonComponentSerializer.gson();
    private static final LegacyComponentSerializer serializer = LegacyComponentSerializer.builder().hexColors().build();
    private static boolean hasPaper;
    private static boolean hasDragonCore;
    private static boolean canHaxColor;
    private static final BukkitAudiences audiences;
    private static boolean debug = false;

    public static void init() {
    }

    public KyoriChatModule(String papi) {
        super(papi);
        hasPaper = PaperUtil.hasPaper();
    }

    public static void setHasDragonCore(boolean isEnable) {
        hasDragonCore = isEnable;
    }

    public static void canHaxColor(boolean b) {
        canHaxColor = b;
    }
    public static boolean canHaxColor() {
        return canHaxColor;
    }

    public static void debug(boolean b) {
        debug = b;
    }

    public Component kyoriComponent(OfflinePlayer player, String msg) {
        return serializer.deserialize(super.msg(player, msg.replace('&', '§'), hasDragonCore || hasPaper || canHaxColor));
    }

    public Component kyoriHover(OfflinePlayer player, Component msg) {
        String value;
        if (this.isPerm && player.isOnline()) {
            Player online = player.getPlayer();
            value = online.hasPermission(this.condition) ? "action" : "default";
        } else {
            value = PlaceholderAPI.setPlaceholders(player, this.condition);
        }

        List<String> list = this.hover.get(value);
        if (list == null) {
            list = this.hover.get("default");
            if (list == null || list.isEmpty()) {
                return msg;
            }
        }

        Component text = Component.text().build();
        int size = list.size();
        int i;
        if (!hasPaper && !hasDragonCore) {
            for(i = 0; i < size; ++i) {
                text = text.append(serializer.deserialize(PlaceholderAPI.setPlaceholders(player, list.get(i))));
                if (i != size - 1) {
                    text = (text).appendNewline();
                }
            }
        } else {
            for(i = 0; i < size; ++i) {
                text = (text).append(serializer.deserialize(ColorUtil.parseColor(PlaceholderAPI.setPlaceholders(player, list.get(i)))));
                if (i != size - 1) {
                    text = (text).appendNewline();
                }
            }
        }

        return msg.hoverEvent(HoverEvent.showText(text));
    }

    public Component kyoriClick(OfflinePlayer player, Component msg) {
        String Msg = this.getModuleMsg(player, this.click);
        return Msg != null && !Msg.isEmpty() ? msg.clickEvent(ClickEvent.clickEvent(Action.SUGGEST_COMMAND, PlaceholderAPI.setPlaceholders(player, Msg.replaceAll("\\[", "").replaceAll("]", "")))) : msg;
    }

    public static void sendMessage(OfflinePlayer player, Component msg, String channel) {
        Set<Player> playerSet = ChatManager.PLAYER_CHAT_CHANNEL_SET.get(channel);
        Sound sound = ChatManager.getSound(player);
        if (playerSet != null){
            if (sound == null){
                playerSet.forEach((p) -> {
                    Audience a = audiences.player(p);
                    a.sendMessage(msg);
                });
            }else {
                playerSet.forEach((p) -> {
                    Audience a = audiences.player(p);
                    a.sendMessage(msg);
                    p.playSound(p.getLocation(), sound, 1, 1);
                });
            }
        }


    }

    public static void sendMessage(BaseComponent[] msg, String channel) {
        Set<Player> playerSet = ChatManager.PLAYER_CHAT_CHANNEL_SET.get(channel);
        if (playerSet != null) {
            playerSet.forEach((p) -> p.spigot().sendMessage(msg));
        }
    }
    public static String assembleAndBroadcast(List<String> format, OfflinePlayer player, String msg, Map<String, KyoriChatModule> moduleMap, boolean isLocal, PlayerChatPacket packet, String channel) {
        if (moduleMap != null && !moduleMap.isEmpty()) {
            if (debug && packet != null) {
                BukkitMessage.log(String.format("[debug]广播消息包(%s)", packet.player()));
            }
            LoggerUtil.info(String.format("[CHAT][%s] %s", player.getName(), msg));
            TextComponent components;
            if (isLocal) {
                KyoriChatModule module;
                if (hasPaper || canHaxColor) {
                    components = Component.text().build();
                    for (String part : format) {
                        module = moduleMap.get(part);
                        if (module != null) {
                            Component component = module.kyoriComponent(player, msg);
                            component = module.kyoriHover(player, component);
                            component = module.kyoriClick(player, component);
                            components = components.append(component);
                        } else {
                            components = components.append(Component.text(part));
                        }
                    }

                    sendMessage(player, components, channel);
                    return serializer2Gson.serialize(components);
                } else {
                    List<BaseComponent> componentList = new ArrayList<>();
                    for (String part : format) {
                        module = moduleMap.get(part);
                        if (module != null) {
                            BaseComponent component = module.component(player, msg, hasDragonCore);
                            module.hover(player, component, hasDragonCore);
                            module.click(player, component);
                            componentList.add(component);
                        } else {
                            componentList.add(new net.md_5.bungee.api.chat.TextComponent(part));
                        }
                    }

                    BaseComponent[] array = componentList.toArray(new BaseComponent[0]);
                    sendMessage(componentList.toArray(array), channel);
                    return ComponentSerializer.toString(array);
                }
            } else {
                if (packet != null) {
                    components = Component.text().build();
                    if (hasPaper) {
                        components = components.append(serializer2Gson.deserialize(packet.json()));
                        sendMessage(player, components, channel);
                    } else if (hasDragonCore || canHaxColor) {
                        System.out.println(packet.json());
                        sendMessage(ComponentSerializer.parse(packet.json()), channel);
                    }
                }

                return null;
            }
        } else {
            return null;
        }
    }

    static {
        audiences = BukkitAudiences.create(BukkitCore.instance);
    }
}
