package fr.onecraft.clientstats.common.base;

public interface Configurable {

    void saveDefaultConfig();

    void saveConfig();

    void reloadConfig();

    void migrate();

    void options();

    String getConfigString(String path);

    String getConfigString(String path, String def);

    void setConfigValue(String path, Object value);

}
