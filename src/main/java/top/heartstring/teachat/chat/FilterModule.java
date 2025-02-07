package top.heartstring.teachat.chat;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;
import top.heartstring.teachat.config.Node;

public class FilterModule implements Node {
    private final Map<String, String> config = new HashMap();

    public FilterModule() {
        this.register();
    }

    public String run(String message) {
        String key;
        for(Iterator var2 = this.config.keySet().iterator(); var2.hasNext(); message = message.replace(key, (CharSequence)this.config.get(key))) {
            key = (String)var2.next();
        }

        return message;
    }

    public void sectionBus(Map<String, ConfigurationSection> sectionMap) {
        ConfigurationSection section = (ConfigurationSection)sectionMap.get("filter");
        if (section != null) {
            this.config.clear();
            section.getKeys(false).forEach((key) -> {
                this.config.put(key, section.getString(key));
            });
        }

    }
}
