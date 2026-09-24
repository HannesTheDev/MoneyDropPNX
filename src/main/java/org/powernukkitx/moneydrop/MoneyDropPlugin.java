package org.powernukkitx.moneydrop;


import org.powernukkitx.moneydrop.command.MoneyDropCommand;
import org.powernukkitx.plugin.PluginBase;
import org.powernukkitx.utils.TextFormat;

public class MoneyDropPlugin extends PluginBase {

    public static MoneyDropPlugin INSTANCE;

    @Override
    public void onLoad() {
        INSTANCE = this;
        this.getServer().getCommandMap().register("exampleplugin", new MoneyDropCommand());

        this.getLogger().info(TextFormat.WHITE + "Loading MoneyDrop!");
    }

    @Override
    public void onEnable() {
        this.getLogger().info(TextFormat.DARK_GREEN + "MoneyDrop succsessfully enabled!");
    }

    public static MoneyDropPlugin get() {
        return INSTANCE;
    }

    @Override
    public void onDisable() {
        this.getLogger().info(TextFormat.DARK_RED + "MoneyDrop successfully disabled!");
    }
}
