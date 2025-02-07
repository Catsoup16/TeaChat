package top.heartstring.teachat.network.packet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class PlayerPacket implements Serializable {
    private final String player;
    private final String uuid;

    public PlayerPacket(String player, String uuid) {
        this.player = player;
        this.uuid = uuid;
    }

    public String player() {
        return this.player;
    }

    public String uuid() {
        return this.uuid;
    }

    public static PlayerPacket deserialize(byte[] data) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            ObjectInputStream ois = new ObjectInputStream(bis);
            Object o = ois.readObject();
            bis.close();
            ois.close();
            return o instanceof PlayerPacket ? (PlayerPacket)o : null;
        } catch (ClassNotFoundException | IOException var4) {
            Exception e = var4;
            throw new RuntimeException(e);
        }
    }

    public byte[] serialize() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(this);
            byte[] data = bos.toByteArray();
            oos.close();
            bos.close();
            return data;
        } catch (IOException var4) {
            IOException e = var4;
            throw new RuntimeException(e);
        }
    }
}
