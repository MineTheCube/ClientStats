package fr.onecraft.clientstats.bungee;

import fr.onecraft.clientstats.bungee.config.PluginConfigurable;
import fr.onecraft.core.helper.Strings;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.junit.Assert.assertEquals;

@Ignore
public class ConfigurationTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    public static final String PLUGIN_CONFIG = Strings.join('\n',
            "# 1. header",
            "# 2. header",
            "# 3. header",
            "map:",
            "- value1",
            "- value2",
            "very:",
            "  deep:",
            "    a: 1",
            "    b: 2",
            "test: false",
            ""
    );

    public static final String USER_CONFIG = Strings.join('\n',
            "# WRONG HEADER",
            "map:",
            "- value1",
            "very:",
            "  deep:",
            "    a: 1.2",
            "test: 'ok'",
            ""
    );

    public static final String EXPECTED_CONFIG = Strings.join('\n',
            "# 1. header",
            "# 2. header",
            "# 3. header",
            "map:",
            "- value1",
            "very:",
            "  deep:",
            "    a: 1.2",
            "    b: 2",
            "test: false",
            ""
    );

    public static final boolean PRINT_DEBUG = false;

    @Test
    public void config() throws IOException {

        // Load test class
        PluginConfigurableTest test = new PluginConfigurableTest();

        // Load config
        ConfigurationProvider provider = ConfigurationProvider.getProvider(YamlConfiguration.class);
        Configuration defaults = provider.load(PLUGIN_CONFIG);
        Configuration config = provider.load(USER_CONFIG);

        // Test missing value
        println("---------------  TEST  ---------------");
        println("vey.deep.b: " + config.get("very.deep.b"));

        // Copy defaults
        println();
        println("--------------- DEFAULT ---------------");
        println("Before: " + config.get("test"));
        test.copyDefaults(config, defaults);
        println("After: " + config.get("test"));

        // Save to file
        File file = folder.newFile("config.yml");
        provider.save(config, file);

        // Copy header
        println();
        println("--------------- HEADER ---------------");

        InputStream stream = new ByteArrayInputStream(PLUGIN_CONFIG.getBytes(StandardCharsets.UTF_8));
        println("Copy header: " + test.copyHeader(stream, file));

        // Print config
        println();
        println("--------------- CONFIG ---------------");
        String result = new String(Files.readAllBytes(file.toPath()));
        println(result + "<");

        assertEquals(EXPECTED_CONFIG, result);

    }

    private void println(String line) {
        if (PRINT_DEBUG) println(line);
    }

    private void println() {
        if (PRINT_DEBUG) println();
    }

    private static class PluginConfigurableTest extends PluginConfigurable {
        @Override
        public boolean copyHeader(InputStream resourceConfig, File toConfig) {
            return super.copyHeader(resourceConfig, toConfig);
        }
        @Override
        public void copyDefaults(Configuration input, Configuration def) {
            super.copyDefaults(input, def);
        }
    }

}
