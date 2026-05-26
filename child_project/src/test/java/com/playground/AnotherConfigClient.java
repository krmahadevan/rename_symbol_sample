package com.playground;

import test.config.ConfigManager;

public class AnotherConfigClient {
    private final ConfigManager configManager;

    public ConfigClient(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public void doSomething() {
        String config = configManager.getConfig("key");
        System.out.println(config);
    }

    public void updateConfig(String value) {
        configManager.setConfig("mykey", value);
    }

    public ConfigManager getManager() {
        return configManager;
    }

    public void printManager() {
        System.out.println(configManager);
    }
}
