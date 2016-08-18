package fr.onecraft.clientstats.bungee;

import fr.onecraft.clientstats.bungee.config.PluginConfigurable;
import fr.onecraft.core.helper.Strings;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import static org.junit.Assert.assertEquals;

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
            "test: false",
            "very:",
            "  deep:",
            "    a: 1",
            "    b: 2",
            "    c: 3",
            ""
    );

    public static final String USER_CONFIG = Strings.join('\n',
            "# WRONG HEADER",
            "map:",
            "- value1",
            "very:",
            "  deep:",
            "    user-added-path: true",
            "    c: '100.001'",
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
            "test: false",
            "very:",
            "  deep:",
            "    a: 1.2",
            "    b: 2",
            "    c: 3",
            "    user-added-path: true",
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

        // Set config destination
        File file = folder.newFile("config.yml");

        // Test missing value
        println("---------------  TEST  ---------------");
        println("vey.deep.b: " + config.get("very.deep.b"));

        // Copy defaults
        println();
        println("--------------- DEFAULT ---------------");
        println("Before: " + config.get("test"));
        test.copyDefaults(config, defaults);
        println("After: " + config.get("test"));

        // Copy header
        println();
        println("--------------- HEADER ---------------");

        InputStream stream = new ByteArrayInputStream(PLUGIN_CONFIG.getBytes(StandardCharsets.UTF_8));
        println("Copy header: " + test.copyHeader(test.parseHeader(stream), file));

        // Save to file
        println();
        println("--------------- CONFIG ---------------");
        provider.save(config, new FileWriter(file, true));
        String result = new String(Files.readAllBytes(file.toPath()));
        println(result + "<");

        assertEquals(EXPECTED_CONFIG, result.replaceAll("\r?\n", "\n"));

    }

    private void println(String line) {
        if (PRINT_DEBUG) System.out.println(line);
    }

    private void println() {
        if (PRINT_DEBUG) System.out.println();
    }

    private static class PluginConfigurableTest extends PluginConfigurable {

        @Override
        public boolean copyHeader(List<String> header, File toConfig) {
            return super.copyHeader(header, toConfig);
        }

        @Override
        protected List<String> parseHeader(InputStream resourceConfig) {
            return super.parseHeader(resourceConfig);
        }

        @Override
        public void copyDefaults(Configuration input, Configuration def) {
            super.copyDefaults(input, def);
        }

    }

}
