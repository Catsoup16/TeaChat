package top.heartstring.teachat.network.packet;

public class PlayerChatPacket extends PlayerPacket {
    private final String msg;
    private final String json;
    private final String channel;

    public PlayerChatPacket(String player, String uuid, String msg, String json, String channel) {
        super(player, uuid);
        this.msg = msg;
        this.json = json;
        this.channel = channel;
    }

    public String msg() {
        return this.msg;
    }

    public String json() {
        return this.json;
    }

    public boolean checkChannel(String channel) {
        return channel.equalsIgnoreCase(this.channel);
    }

    public String channel() {
        return this.channel;
    }
}
