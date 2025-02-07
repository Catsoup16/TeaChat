package top.heartstring.teachat.config;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.md_5.bungee.config.Configuration;

public interface BCNode {
    Set<BCNode> Nodes = new HashSet();

    void loadBus(Map<String, Configuration> var1);

    default void registerNode() {
        Nodes.add(this);
    }
}
