//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package top.heartstring.teachat.network.packet;

public class PlayerTitlePacket extends PlayerPacket {
    private final String title;
    private final String subtitle;
    private final String sender;
    private final boolean isServer;

    public PlayerTitlePacket(boolean isServer, String player, String sender, String title, String subtitle) {
        super(player, (String)null);
        this.title = title;
        this.subtitle = subtitle;
        this.sender = sender;
        this.isServer = isServer;
    }

    public String title() {
        return this.title;
    }

    public String subtitle() {
        return this.subtitle;
    }

    public String sender() {
        return this.sender;
    }

    public boolean isServer() {
        return this.isServer;
    }
}
