//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package top.heartstring.teachat.network;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.connection.PluginMessageEvent.ForwardResult;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.ChannelMessageSource;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.util.Collection;
import java.util.Optional;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import top.heartstring.teachat.VelocityCore;
import top.heartstring.teachat.network.packet.PlayerChatPacket;
import top.heartstring.teachat.network.packet.PlayerOptionPacket;
import top.heartstring.teachat.network.packet.PlayerPacket;
import top.heartstring.teachat.network.packet.PlayerTitlePacket;
import top.heartstring.teachat.utils.VelocityMessage;

public class VelocityNetworking {
    private final ProxyServer server;
    public static final String identifier = "teachat:chat";
    public static final String option_identifier = "teachat:option";
    public static final String title_identifier = "teachat:title";
    public static final MinecraftChannelIdentifier IDENTIFIER = MinecraftChannelIdentifier.from("teachat:chat");
    public static final MinecraftChannelIdentifier OPTION_IDENTIFIER = MinecraftChannelIdentifier.from("teachat:option");
    public static final MinecraftChannelIdentifier TITLE_IDENTIFIER = MinecraftChannelIdentifier.from("teachat:title");

    public VelocityNetworking(ProxyServer proxyServer) {
        this.server = proxyServer;
        this.server.getChannelRegistrar().register(IDENTIFIER);
        this.server.getChannelRegistrar().register(OPTION_IDENTIFIER);
        this.server.getChannelRegistrar().register(TITLE_IDENTIFIER);
    }

    @Subscribe
    public void onPacket(PluginMessageEvent event) {
        ChannelMessageSource source = event.getSource();
        if (source instanceof ServerConnection) {
            String id = event.getIdentifier().getId();
            ServerConnection connection = (ServerConnection)source;
            switch (id) {
                case identifier:
                    byte[] data = event.getData();
                    PlayerPacket packet = PlayerPacket.deserialize(data);
                    if (packet instanceof PlayerChatPacket) {
                        PlayerChatPacket chatPacket = (PlayerChatPacket)packet;
                        String message = String.format("[%s|%s][%s] %s", connection.getServerInfo().getName(), chatPacket.channel(), chatPacket.player(), chatPacket.msg());
                        VelocityMessage.log(message);
                        VelocityMessage.record(message);
                        Collection<RegisteredServer> servers = this.server.getAllServers();
                        for(RegisteredServer node : servers) {
                            String name = node.getServerInfo().getName();
                            if (!name.equals(connection.getServerInfo().getName())) {
                                node.sendPluginMessage(IDENTIFIER, data);
                            }
                        }
                    }

                    event.setResult(ForwardResult.handled());
                    break;
                case option_identifier:
                    byte[] optionData = event.getData();
                    PlayerPacket packet1 = PlayerPacket.deserialize(optionData);
                    if (packet1 instanceof PlayerOptionPacket) {
                        PlayerOptionPacket optionPacket = (PlayerOptionPacket)packet1;
                        Collection<RegisteredServer> servers = this.server.getAllServers();
                        if (optionPacket.channel == null) {
                            optionPacket.channel = VelocityCore.getChannel(optionPacket.player());
                            VelocityMessage.log(String.format("%s请求了频道:%s", optionPacket.player(), optionPacket.channel));
                            for(RegisteredServer node : servers) {
                                node.sendPluginMessage(OPTION_IDENTIFIER, optionPacket.serialize());
                            }
                        } else {
                            VelocityCore.setChannel(optionPacket.player(), optionPacket.channel);
                            VelocityMessage.log(String.format("%s改变了频道:%s", optionPacket.player(), optionPacket.channel));

                            for(RegisteredServer node : servers) {
                                node.sendPluginMessage(OPTION_IDENTIFIER, optionData);
                            }
                        }
                    }

                    event.setResult(ForwardResult.handled());
                    break;
                case title_identifier:
                    PlayerPacket packet2 = PlayerPacket.deserialize(event.getData());
                    if (packet2 instanceof PlayerTitlePacket) {
                        PlayerTitlePacket titlePacket = (PlayerTitlePacket)packet2;
                        Title title = Title.title(Component.text(titlePacket.title()), Component.text(titlePacket.subtitle()));
                        Optional<Player> receiver;
                        if (titlePacket.isServer()) {
                            String player = titlePacket.player();
                            if (player != null && !player.equalsIgnoreCase("all")) {
                                receiver = this.server.getPlayer(titlePacket.player());
                                if (receiver.isPresent()) {
                                    receiver.get().showTitle(title);
                                    VelocityMessage.msg(this.server.getConsoleCommandSource(), String.format(" §a你向 §e%s§a 发送了屏幕标题", titlePacket.player()));
                                    VelocityMessage.msg(receiver.get(), " §a服务器向你发送了屏幕标题");
                                } else {
                                    VelocityMessage.msg(this.server.getConsoleCommandSource(), String.format(" §f%s§c 不存在或已离线", titlePacket.player()));
                                }
                            } else {
                                this.server.getAllPlayers().forEach((p) -> {
                                    p.showTitle(title);
                                    VelocityMessage.msg(p, " §a服务器向您发送了屏幕标题");
                                });
                                event.setResult(ForwardResult.handled());
                            }
                            return;
                        }

                        Optional<Player> sender = this.server.getPlayer(titlePacket.sender());
                        receiver = this.server.getPlayer(titlePacket.player());
                        if (sender.isPresent()) {
                            Player player = sender.get();
                            if (receiver.isPresent()) {
                                Player receiverPlayer = receiver.get();
                                receiverPlayer.showTitle(title);
                                VelocityMessage.msg(player, String.format(" §a你向 §e%s§a 发送了屏幕标题", titlePacket.player()));
                                VelocityMessage.msg(receiverPlayer, String.format(" §e%s §a向你发送了屏幕标题", titlePacket.sender()));
                            } else {
                                VelocityMessage.msg(player, String.format(" §f%s§c 不存在或已离线", titlePacket.player()));
                            }
                        }
                    }

                    event.setResult(ForwardResult.handled());
            }
        }

    }
}
