//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package top.heartstring.teachat.network;

import java.util.Collection;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import top.heartstring.teachat.BungeeCore;
import top.heartstring.teachat.network.packet.PlayerChatPacket;
import top.heartstring.teachat.network.packet.PlayerOptionPacket;
import top.heartstring.teachat.network.packet.PlayerPacket;
import top.heartstring.teachat.network.packet.PlayerTitlePacket;
import top.heartstring.teachat.utils.BungeeMessage;

public class BungeeNetworking implements Listener {
    public static final String CHANNEL = "teachat:chat";
    public static final String OPTION_CHANNEL = "teachat:option";
    public static final String TITLE_CHANNEL = "teachat:title";
    private static BungeeCore p;

    public BungeeNetworking() {
    }

    public static void init(BungeeCore plugin) {
        p = plugin;
        plugin.getProxy().registerChannel("teachat:chat");
        plugin.getProxy().registerChannel("teachat:option");
        plugin.getProxy().registerChannel("teachat:title");
    }

    @EventHandler
    public void onPacket(PluginMessageEvent event) {
        switch (event.getTag()) {
            case CHANNEL:
                byte[] data = event.getData();
                PlayerPacket packet = PlayerPacket.deserialize(data);
                if (packet instanceof PlayerChatPacket) {
                    PlayerChatPacket chatPacket = (PlayerChatPacket)packet;
                    String message = String.format("[%s][%s] %s", chatPacket.channel(), chatPacket.player(), chatPacket.msg());
                    p.getLogger().info(message);
                    BungeeMessage.record(message);
                    Collection<ServerInfo> values = ProxyServer.getInstance().getServersCopy().values();
                    for (ServerInfo info : values) {
                        if (!info.getAddress().equals(event.getSender().getAddress())) {
                            info.sendData("teachat:chat", data, true);
                        }
                    }
                }

                event.setCancelled(true);
                break;
            case OPTION_CHANNEL:
                byte[] optionData = event.getData();
                PlayerPacket option = PlayerPacket.deserialize(optionData);
                if (option instanceof PlayerOptionPacket) {
                    PlayerOptionPacket optionPacket = (PlayerOptionPacket)option;
                    if (optionPacket.channel == null) {
                        optionPacket.channel = BungeeCore.getChannel(optionPacket.player());
                        BungeeMessage.log(String.format("%s请求频道:%s", optionPacket.player(), optionPacket.channel));
                        Collection<ServerInfo> values = ProxyServer.getInstance().getServersCopy().values();

                        for (ServerInfo info : values) {
                            info.sendData("teachat:option", optionPacket.serialize(), true);
                        }
                    } else {
                        BungeeCore.setChannel(optionPacket.player(), optionPacket.channel);
                        BungeeMessage.log(String.format("%s改变频道:%s", optionPacket.player(), optionPacket.channel));
                        Collection<ServerInfo> values = ProxyServer.getInstance().getServersCopy().values();
                        for (ServerInfo info : values) {
                            info.sendData("teachat:option", optionData, true);
                        }
                    }
                }

                event.setCancelled(true);
                break;
            case TITLE_CHANNEL:
                PlayerPacket packet2 = PlayerPacket.deserialize(event.getData());
                if (packet2 instanceof PlayerTitlePacket) {
                    PlayerTitlePacket titlePacket = (PlayerTitlePacket)packet2;
                    ProxyServer proxy = p.getProxy();
                    Title title = proxy.createTitle().title(new TextComponent(titlePacket.title()), new TextComponent(titlePacket.subtitle()));
                    ProxiedPlayer sender;
                    if (titlePacket.isServer()) {
                        String player = titlePacket.player();
                        if (player != null && !player.equalsIgnoreCase("all")) {
                            sender = proxy.getPlayer(titlePacket.player());
                            if (sender != null) {
                                sender.sendTitle(title);
                                BungeeMessage.msg(proxy.getConsole(), String.format(" §a你向 §e%s§a 发送了屏幕标题", titlePacket.player()));
                                BungeeMessage.msg(sender, " §a服务器向你发送了屏幕标题");
                            } else {
                                BungeeMessage.msg(proxy.getConsole(), String.format(" §f%s§c 不存在或已离线", titlePacket.player()));
                            }
                        } else {
                            proxy.getPlayers().forEach((p) -> {
                                p.sendTitle(title);
                                BungeeMessage.msg(p, " §a服务器向您发送了屏幕标题");
                            });
                        }
                        return;
                    }

                    ProxiedPlayer receiver = proxy.getPlayer(titlePacket.player());
                    sender = proxy.getPlayer(titlePacket.sender());
                    if (sender != null) {
                        if (receiver != null) {
                            receiver.sendTitle(proxy.createTitle().title(new TextComponent(titlePacket.title())).subTitle(new TextComponent(titlePacket.subtitle())));
                            BungeeMessage.msg(sender, String.format(" §a你向 §e%s§a 发送了屏幕标题", titlePacket.player()));
                            BungeeMessage.msg(receiver, String.format(" §e%s §a向你发送了屏幕标题", titlePacket.sender()));
                        } else {
                            BungeeMessage.msg(sender, String.format(" §f%s§c 不存在或已离线", titlePacket.player()));
                        }
                    }
                }

                event.setCancelled(true);
        }

    }
}
