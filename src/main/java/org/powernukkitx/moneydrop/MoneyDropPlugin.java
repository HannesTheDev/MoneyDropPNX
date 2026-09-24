package org.powernukkitx.moneydrop;

import org.powernukkitx.moneydrop.command.MoneyDropCommand;
import org.powernukkitx.plugin.PluginBase;
import org.powernukkitx.utils.Config;
import org.powernukkitx.utils.TextFormat;

public class MoneyDropPlugin extends PluginBase {

    public static MoneyDropPlugin INSTANCE;

    private Config messages;

    @Override
    public void onEnable() {
        INSTANCE = this;

        saveResource("messages.yml");

        messages = new Config(getDataFolder() + "/messages.yml", Config.YAML);

        this.getLogger().info(TextFormat.DARK_GREEN + "MoneyDrop succsessfully enabled!");
        this.getServer().getCommandMap().register("moneydrop", new MoneyDropCommand());
    }

    public static MoneyDropPlugin get() {
        return INSTANCE;
    }

    public String getMessage(String key) {
        String message = messages.getString(key);

        if (message == null) {
            return "§cMessage not found: " + key;
        }

        return message.replace("&", "§");
    }
}
