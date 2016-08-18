package fr.onecraft.clientstats.bungee.config;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import fr.onecraft.clientstats.common.base.Configurable;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public abstract class PluginConfigurable extends Plugin implements Configurable {

    private final String filename = "config.yml";
    private Configuration defaults = null;
    private Configuration config = null;

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void saveDefaultConfig() {

        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        File file = new File(getDataFolder(), filename);

        if (!file.exists()) {
            try (InputStream in = getResourceAsStream(filename)) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Configuration getConfig() {
        if (config == null) {
            reloadConfig();
        }
        return config;
    }

    @Override
    public void saveConfig() {
        try {
            File configFile = new File(getDataFolder(), filename);
            ConfigurationProvider provider = ConfigurationProvider.getProvider(YamlConfiguration.class);
            provider.save(config, configFile);
            if (!copyHeader(getResourceAsStream(filename), configFile)) {
                // Failed to copy header, save config again
                provider.save(config, configFile);
            }

        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Could not save config to " + filename, e);
        }
    }

    @Override
    public void reloadConfig() {
        try {
            ConfigurationProvider provider = ConfigurationProvider.getProvider(YamlConfiguration.class);
            defaults = provider.load(getResourceAsStream(filename));
            config = provider.load(new File(getDataFolder(), filename));
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Can't load config.yml !", e);
        }
    }

    @Override
    public void migrate() {}

    @Override
    public void options() {
        // No option with Bungeecord API, we do it ourselves
        // Note: Header is added on save
        copyDefaults(config, defaults);
    }

    @Override
    public String getConfigString(String path) {
        return getConfig().getString(path);
    }

    @Override
    public String getConfigString(String path, String def) {
        return getConfig().getString(path, def);
    }

    protected static final String COMMENT_PREFIX = "# ";

    protected boolean copyHeader(InputStream resourceConfig, File toConfig) {

        // resourceConfig is from plugin, so it should be UTF-8
        try (BufferedReader input = new BufferedReader(new InputStreamReader(resourceConfig, Charsets.UTF_8))) {

            // Read header
            List<String> header = Lists.newArrayList();
            String line;
            while ((line = input.readLine()) != null) {
                if (line.startsWith(COMMENT_PREFIX)) {
                    if (line.length() > COMMENT_PREFIX.length()) {
                        header.add(line);
                    }
                } else if (line.length() > 0) {
                    break;
                }
            }

            // Read config file
            byte[] content = Files.readAllBytes(toConfig.toPath());

            // Write header with system line breaks
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(toConfig))) {
                for (String s : header) {
                    writer.write(s);
                    writer.newLine();
                }
            }

            // Copy rest of config as it to preserve line breaks
            try (FileOutputStream out = new FileOutputStream(toConfig, true)) {
                out.write(content);
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    protected void copyDefaults(Configuration input, Configuration def) {

        // Get keys of current path
        Collection<String> inputKeys = input.getKeys();
        Collection<String> defKeys = def.getKeys();

        // Looping through default keys
        for (String key : defKeys) {
            if (!inputKeys.contains(key)) {
                // Missing key in user configuration
                input.set(key, def.get(key));
            } else {
                // Get values
                Object inputValue = input.get(key);
                Object defValue = def.get(key);

                // Sometimes a Map is returned instead of a Configuration
                Configuration inputSection = inputValue instanceof Map ? input.getSection(key) : null;
                Configuration defSection = defValue instanceof Map ? def.getSection(key) : null;

                if (defSection != null) {
                    // There is a default section
                    if (inputSection != null) {
                        // And a user section, so apply default into it
                        copyDefaults(inputSection, defSection);
                    } else {
                        // But no user section, so just copy it
                        input.set(key, defSection);
                    }
                }

                if (inputValue == null) {
                    // No user value
                    input.set(key, defValue);
                } else if (defValue instanceof Configuration) {
                    // There is a default section
                    if (inputValue instanceof Configuration) {
                        // And a user section, so apply default into it
                        copyDefaults((Configuration) inputValue, (Configuration) defValue);
                    } else {
                        // But no user section, so just copy it
                        input.set(key, defValue);
                    }
                } else if (!inputValue.getClass().equals(defValue.getClass())) {
                    // Values are not the same type
                    // We get the base class, as for instance Integer ≠ Float but both are Numbers
                    Class inputClass = getBaseClass(inputValue);
                    Class defClass = getBaseClass(defValue);
                    if (inputClass == null || !inputClass.equals(defClass)) {
                        // Still different types, so override user value
                        input.set(key, defValue);
                    }
                }
            }
        }
    }

    private Class getBaseClass(Object object) {
        Class<?>[] classes = { Configuration.class, Boolean.class, Number.class, Character.class, String.class, List.class };
        for (Class<?> clazz : classes) {
            if (clazz.isInstance(object)) {
                return clazz;
            }
        }
        return null;
    }

}
