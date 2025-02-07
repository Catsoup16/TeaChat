//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package top.heartstring.teachat.dependency;

import com.alessiodp.libby.Library;
import com.alessiodp.libby.LibraryManager;
import com.alessiodp.libby.classloader.URLClassLoaderHelper;
import com.alessiodp.libby.logging.adapters.JDKLogAdapter;
import com.alessiodp.libby.logging.adapters.LogAdapter;
import java.io.InputStream;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Objects;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class DependencyManager extends LibraryManager {
    private final URLClassLoaderHelper classLoader;
    private final Plugin plugin;

    private DependencyManager(Plugin plugin, LogAdapter logAdapter, Path dataDirectory, String directoryName) {
        super(logAdapter, dataDirectory, directoryName);
        this.plugin = plugin;
        this.classLoader = new URLClassLoaderHelper((URLClassLoader)plugin.getClass().getClassLoader(), this);
    }

    public static void init(JavaPlugin plugin) {
        try {
            Path path = plugin.getDataFolder().toPath().toAbsolutePath().getParent().getParent();
            DependencyManager manager = new DependencyManager(plugin, new JDKLogAdapter(((Plugin)Objects.requireNonNull(plugin, "plugin")).getLogger()), path, "libraries");
            Library adventureAPI = Library.builder().groupId("net{}kyori").artifactId("adventure-api").version("4.17.0").loaderId("teachat-lib").build();
            Library adventureLegacy = Library.builder().groupId("net{}kyori").artifactId("adventure-text-serializer-legacy").version("4.17.0").loaderId("teachat-lib").build();
            Library adventureGson = Library.builder().groupId("net{}kyori").artifactId("adventure-text-serializer-gson").version("4.17.0").loaderId("teachat-lib").build();
            Library adventureJson = Library.builder().groupId("net{}kyori").artifactId("adventure-text-serializer-json").version("4.17.0").loaderId("teachat-lib").build();
            Library adventureExamination = Library.builder().groupId("net{}kyori").artifactId("examination-api").version("1.3.0").loaderId("teachat-lib").build();
            Library kyoriOption = Library.builder().groupId("net{}kyori").artifactId("option").version("1.0.0").loaderId("teachat-lib").build();
            Library adventureKey = Library.builder().groupId("net{}kyori").artifactId("adventure-key").version("4.17.0").loaderId("teachat-lib").build();
            Library adventurePlatform = Library.builder().groupId("net{}kyori").artifactId("adventure-platform-bukkit").version("4.3.4").loaderId("teachat-lib").build();
            manager.addMavenCentral();
            manager.loadLibraries(adventureAPI, adventureLegacy, adventureGson, adventureJson, adventureExamination, kyoriOption, adventureKey, adventurePlatform);
        } catch (Exception var10) {
            Exception e = var10;
            throw new RuntimeException(e);
        }
    }

    protected void addToClasspath(@NotNull Path file) {
        this.classLoader.addToClasspath(file);
    }

    protected InputStream getResourceAsStream(@NotNull String path) {
        return this.plugin.getResource(path);
    }
}
