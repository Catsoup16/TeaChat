package top.heartstring.teachat.network.packet;

public class PlayerOptionPacket extends PlayerPacket {
    public String channel;

    public PlayerOptionPacket(String player, String uuid) {
        super(player, uuid);
    }
}
