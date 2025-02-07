
package top.heartstring.teachat.chat;

import com.google.common.collect.ImmutableList;
import io.netty.util.internal.ConcurrentSet;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import top.heartstring.teachat.BukkitCore;
import top.heartstring.teachat.config.Config;
import top.heartstring.teachat.config.Node;
import top.heartstring.teachat.network.BukkitNetworking;
import top.heartstring.teachat.network.packet.PlayerChatPacket;
import top.heartstring.teachat.network.packet.PlayerOptionPacket;
import top.heartstring.teachat.utils.BukkitMessage;
import top.heartstring.teachat.utils.LoggerUtil;

public class ChatManager implements Node, Listener {
    private static final Map<String, List<String>> formatMap = new HashMap<>();
    private static final Map<String, Map<String, KyoriChatModule>> ModuleMap = new HashMap<>();
    private static String SOUND_PAPI;
    private static Map<String, Sound> soundMap = new HashMap<>();
    private static boolean proxy = false;
    private static boolean DragonCore = false;
    private static final FilterModule filter = new FilterModule();
    private static String DEFAULT_CHAT_CHANNEL;
    private static final Set<String> CHAT_CHANNELS = new HashSet<>();
    private static List<String> CHANNELS;
    private static final List<Permission> CHANNEL_PERMISSIONS = new ArrayList<>();
    protected static final Map<String, String> PLAYER_CHAT_CHANNEL_MAP = new ConcurrentHashMap<>();
    protected static final Map<String, Set<Player>> PLAYER_CHAT_CHANNEL_SET = new ConcurrentHashMap<>();
    protected static boolean canHexColor = false;
    public ChatManager() {
        this.register();
    }

    public synchronized void sectionBus(Map<String, ConfigurationSection> sectionMap) {
        ConfigurationSection setting = sectionMap.get("settings");
        ConfigurationSection chatSound = sectionMap.get("chatsound");
        if (setting != null) {
            proxy = setting.getBoolean("proxy", false);
            DEFAULT_CHAT_CHANNEL = setting.getString("defaultChannel", "global");
            boolean hasDragonCore = setting.getBoolean("DragonCore", false) && Bukkit.getPluginManager().isPluginEnabled("DragonCore");
            KyoriChatModule.setHasDragonCore(hasDragonCore);
            String version = BukkitCore.instance.getServer().getBukkitVersion();
            version = version.replace("-R0.1-SNAPSHOT", "");
            canHexColor = version.compareTo("1.16") >= 0;
            KyoriChatModule.canHaxColor(canHexColor);
            KyoriChatModule.debug(setting.getBoolean("debug", false));
            DragonCore = hasDragonCore;
        }
        if (chatSound != null) {
            SOUND_PAPI = chatSound.getString("papi");
            ConfigurationSection actions = chatSound.getConfigurationSection("actions");
            soundMap.clear();
            if (actions != null) {
                actions.getKeys(false).forEach((k)->{
                    try {
                        soundMap.put(k, Sound.valueOf(actions.getString(k).toUpperCase()));
                    }catch (IllegalArgumentException e){
                        if (actions.getString(k).compareToIgnoreCase("none") != 0){
                            LoggerUtil.log(String.format(" §c>没有这个聊天提示音: %s = %s", k, actions.getString(k)));
                        }
                    }
                });
            }
        }
    }

    public static Sound getSound(OfflinePlayer player){
        String s = PlaceholderAPI.setPlaceholders(player, SOUND_PAPI);
        Sound sound = soundMap.get(s);
        if(sound == null){
            sound = soundMap.get("default");
        }
        return sound;
    }

    public synchronized void channelSectionBus(Map<String, Map<String, ConfigurationSection>> channelSectionMap) {
        LoggerUtil.log(" §7>聊天配置开始加载 " + (DragonCore ? "§bDragonCore+" : (KyoriChatModule.canHaxColor() ? String.format("§bcanHexColor:%s",BukkitCore.instance.getServer().getBukkitVersion()):"")));
        Set<String> channelSet = channelSectionMap.keySet();
        ModuleMap.clear();
        formatMap.clear();
        PLAYER_CHAT_CHANNEL_SET.clear();
        CHAT_CHANNELS.clear();

        for (String channel : channelSet) {
            Map<String, ConfigurationSection> sectionMap = channelSectionMap.get(channel);
            Map<String, KyoriChatModule> KyoriChatModuleMap = new HashMap<>();

            for (String key : sectionMap.keySet()) {
                if (key.contains("settings")) {
                    formatMap.put(channel, Arrays.stream(Objects.requireNonNull((sectionMap.get(key)).getString("format", "format")).split("\\|")).collect(Collectors.toList()));
                } else {
                    KyoriChatModule chatModule = new KyoriChatModule(key);
                    chatModule.sectionBus(sectionMap);
                    KyoriChatModuleMap.put("%" + key + "%", chatModule);
                }
            }

            ModuleMap.put(channel, KyoriChatModuleMap);
            PLAYER_CHAT_CHANNEL_SET.put(channel, new ConcurrentSet<>());
            CHAT_CHANNELS.add(channel);
            LoggerUtil.log("  §8| " + channel);
        }

        if (!CHAT_CHANNELS.contains(DEFAULT_CHAT_CHANNEL)) {
            ArrayList<String> list = new ArrayList<>(CHAT_CHANNELS);
            if (list.isEmpty()) {
                BukkitCore.instance.saveResource("channel/global.yml", true);
                Config.load(BukkitCore.instance);
                return;
            }

            DEFAULT_CHAT_CHANNEL = list.get(0);
        }

        CHANNELS = CHAT_CHANNELS.isEmpty() ? ImmutableList.of() : ImmutableList.copyOf(CHAT_CHANNELS);
        PluginManager manager = Bukkit.getPluginManager();
        manager.removePermission("teachat.channel.*");
        for (Permission perm: CHANNEL_PERMISSIONS){
            manager.removePermission(perm);
        }
        CHANNEL_PERMISSIONS.clear();
        for (String channel : CHANNELS) {
            Permission permission = new Permission("teachat.channel." + channel);
            CHANNEL_PERMISSIONS.add(permission);
            manager.addPermission(permission);
        }
        LoggerUtil.log(" §7>聊天配置§b已加载 §8" + channelSet.size() + "个频道");
        Bukkit.getOnlinePlayers().forEach((p) -> BukkitNetworking.getOption(p, false));
    }
    public static synchronized void unload$ChannelPermission(){
        for (Permission perm : CHANNEL_PERMISSIONS){
            Bukkit.getPluginManager().removePermission(perm);
        }
    }
    public static List<String> getChannels() {
        return CHANNELS;
    }

    public static boolean hasPermission(Player player, String channel) {
        return CHAT_CHANNELS.contains(channel) && player.hasPermission("teachat.channel." + channel);
    }

    public static void broadcast(Player sender, String uuid, String message, boolean isLocal, PlayerChatPacket packet, String channel) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
        if (!proxy && sender != null && ModuleMap.get(channel) == null) {
            PLAYER_CHAT_CHANNEL_MAP.put(DEFAULT_CHAT_CHANNEL, sender.getName());
            PLAYER_CHAT_CHANNEL_SET.get(DEFAULT_CHAT_CHANNEL).add(sender);
            channel = DEFAULT_CHAT_CHANNEL;
        }

        String json = KyoriChatModule.assembleAndBroadcast(formatMap.get(channel), player, message, ModuleMap.get(channel), isLocal, packet, channel);
        if (json != null && sender != null && proxy) {
            sender.sendPluginMessage(BukkitCore.instance, "teachat:chat", (new PlayerChatPacket(sender.getDisplayName(), sender.getUniqueId().toString(), message, json, channel)).serialize());
        }

    }

    public static void setPlayerChatChannel(PlayerOptionPacket packet) {
        String channel = packet.channel == null ? DEFAULT_CHAT_CHANNEL : packet.channel;
        channel = CHAT_CHANNELS.contains(channel) ? channel : DEFAULT_CHAT_CHANNEL;
        String replaceChannel = PLAYER_CHAT_CHANNEL_MAP.put(packet.player(), channel);
        Player player = Bukkit.getPlayer(UUID.fromString(packet.uuid()));
        if (player != null) {
            Set<Player> set;
            if (replaceChannel != null) {
                set = PLAYER_CHAT_CHANNEL_SET.get(replaceChannel);
                if (set != null) {
                    set.remove(player);
                }
            }

            set = PLAYER_CHAT_CHANNEL_SET.get(channel);
            if (set != null) {
                set.remove(player);
                set.add(player);
                BukkitMessage.msg(player, "§a接入聊天频道 §b" + channel);
            }

        }
    }

    public static boolean hasChannel(String channel) {
        return CHAT_CHANNELS.contains(channel);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (event.isAsynchronous()) {
            event.setCancelled(true);
            Player player = event.getPlayer();
            broadcast(player, player.getUniqueId().toString(), filter.run(event.getMessage()), true, null, PLAYER_CHAT_CHANNEL_MAP.get(player.getName()));
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        BukkitNetworking.getOption(event.getPlayer(), true);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String name = player.getName();
        String channel = PLAYER_CHAT_CHANNEL_MAP.get(name);
        if (channel != null) {
            PLAYER_CHAT_CHANNEL_MAP.remove(name);
            Set<Player> playerSet = PLAYER_CHAT_CHANNEL_SET.get(channel);
            if (playerSet != null) {
                playerSet.remove(player);
            }
        }

    }
}
