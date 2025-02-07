//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package top.heartstring.teachat.config;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import org.bukkit.configuration.ConfigurationSection;

public interface Node {
    Set<Node> Nodes = new CopyOnWriteArraySet();

    void sectionBus(Map<String, ConfigurationSection> var1);

    default void channelSectionBus(Map<String, Map<String, ConfigurationSection>> channelSectionMap) {
    }

    default void register() {
        Nodes.add(this);
    }
}
