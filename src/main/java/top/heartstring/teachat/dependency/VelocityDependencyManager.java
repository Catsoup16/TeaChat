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
import org.jetbrains.annotations.NotNull;
import top.heartstring.teachat.VelocityCore;

public class VelocityDependencyManager extends LibraryManager {
    private final URLClassLoaderHelper classLoader;

    protected VelocityDependencyManager(VelocityCore core, @NotNull LogAdapter logAdapter, @NotNull Path dataDirectory, @NotNull String directoryName) {
        super(logAdapter, dataDirectory, directoryName);
        this.classLoader = new URLClassLoaderHelper((URLClassLoader)core.getClass().getClassLoader(), this);
    }

    public static void init(VelocityCore core) {
        Path path = core.dataDirectory.toAbsolutePath().getParent().getParent();
        VelocityDependencyManager manager = new VelocityDependencyManager(core, new JDKLogAdapter(core.logger), path, "libraries");
        Library snakeyaml = Library.builder().groupId("org{}yaml").artifactId("snakeyaml").version("2.2").loaderId("teachat-lib").build();
        manager.addMavenCentral();
        manager.loadLibrary(snakeyaml);
    }

    protected void addToClasspath(@NotNull Path file) {
        this.classLoader.addToClasspath(file);
    }

    protected InputStream getResourceAsStream(@NotNull String path) {
        return VelocityCore.class.getClassLoader().getResourceAsStream(path);
    }
}
