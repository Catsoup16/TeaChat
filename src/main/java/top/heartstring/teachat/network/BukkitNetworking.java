//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package top.heartstring.teachat.network;

import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.plugin.messaging.PluginMessageListener;
import top.heartstring.teachat.BukkitCore;
import top.heartstring.teachat.chat.ChatManager;
import top.heartstring.teachat.chat.TitleModule;
import top.heartstring.teachat.config.Node;
import top.heartstring.teachat.network.packet.PlayerChatPacket;
import top.heartstring.teachat.network.packet.PlayerOptionPacket;
import top.heartstring.teachat.network.packet.PlayerPacket;
import top.heartstring.teachat.network.packet.PlayerTitlePacket;
import top.heartstring.teachat.utils.BukkitMessage;

public class BukkitNetworking implements PluginMessageListener, Node {
    public static final String CHANNEL = "teachat:chat";
    public static final String TITLE_CHANNEL = "teachat:title";
    public static final String OPTION_CHANNEL = "teachat:option";
    private static boolean proxy = true;
    private static boolean debug = false;
    private static JavaPlugin P;

    public BukkitNetworking() {
        this.register();
    }

    public static void init(JavaPlugin plugin) {
        P = plugin;
        Messenger messenger = Bukkit.getMessenger();
        messenger.registerOutgoingPluginChannel(plugin, CHANNEL);
        messenger.registerOutgoingPluginChannel(plugin, OPTION_CHANNEL);
        messenger.registerOutgoingPluginChannel(plugin, TITLE_CHANNEL);
        messenger.registerIncomingPluginChannel(plugin, CHANNEL, new BukkitNetworking());
        messenger.registerIncomingPluginChannel(plugin, OPTION_CHANNEL, new BukkitNetworking());
        messenger.registerIncomingPluginChannel(plugin, TITLE_CHANNEL, new BukkitNetworking());
    }

    public static void close(JavaPlugin plugin) {
        Messenger messenger = Bukkit.getMessenger();
        messenger.unregisterOutgoingPluginChannel(plugin, CHANNEL);
        messenger.unregisterOutgoingPluginChannel(plugin, OPTION_CHANNEL);
        messenger.unregisterOutgoingPluginChannel(plugin, TITLE_CHANNEL);
        messenger.unregisterIncomingPluginChannel(plugin, CHANNEL);
        messenger.unregisterIncomingPluginChannel(plugin, OPTION_CHANNEL);
        messenger.unregisterIncomingPluginChannel(plugin, TITLE_CHANNEL);
    }

    public static void getOption(Player player, boolean currentJoin) {
        if (proxy) {
            if (currentJoin) {
                Bukkit.getScheduler().runTaskLater(BukkitCore.instance, () -> {
                    player.sendPluginMessage(P, "teachat:option", (new PlayerOptionPacket(player.getName(), player.getUniqueId().toString())).serialize());
                }, 20L);
            } else {
                player.sendPluginMessage(P, "teachat:option", (new PlayerOptionPacket(player.getName(), player.getUniqueId().toString())).serialize());
            }
        }

    }

    public static void setOption(Player player, PlayerOptionPacket packet) {
        if (proxy) {
            player.sendPluginMessage(P, "teachat:option", packet.serialize());
        } else {
            ChatManager.setPlayerChatChannel(packet);
        }

    }

    public static void sentTitle(CommandSender sender, PlayerTitlePacket packet) {
        if (proxy) {
            if (sender instanceof Player) {
                ((Player)sender).sendPluginMessage(P, "teachat:title", packet.serialize());
            } else {
                Bukkit.getServer().sendPluginMessage(P, "teachat:title", packet.serialize());
            }
        } else if (!TitleModule.send(sender, packet)) {
            BukkitMessage.msg(sender, String.format("§f%s§c 不存在或已离线", packet.player()));
        }

    }

    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (channel.startsWith("teachat")) {
            PlayerPacket packet = PlayerPacket.deserialize(message);
            switch (channel) {
                case "teachat:chat":
                    if (packet instanceof PlayerChatPacket) {
                        PlayerChatPacket chatPacket = (PlayerChatPacket)packet;
                        if (debug) {
                            BukkitMessage.log(String.format("[debug]接收消息包(%s)", chatPacket.player()));
                        }

                        ChatManager.broadcast(null, packet.uuid(), chatPacket.msg(), false, chatPacket, chatPacket.channel());
                    }
                    break;
                case "teachat:option":
                    if (packet instanceof PlayerOptionPacket) {
                        ChatManager.setPlayerChatChannel((PlayerOptionPacket)packet);
                    }
            }

        }
    }

    public void sectionBus(Map<String, ConfigurationSection> sectionMap) {
        ConfigurationSection settings = sectionMap.get("settings");
        if (settings != null) {
            proxy = settings.getBoolean("proxy", false);
            debug = settings.getBoolean("debug", false);
        }

    }
}
