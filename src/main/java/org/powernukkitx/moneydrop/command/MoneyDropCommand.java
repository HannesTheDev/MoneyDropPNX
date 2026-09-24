package org.powernukkitx.moneydrop.command;


import org.powernukkitx.Player;
import org.powernukkitx.command.CommandSender;
import org.powernukkitx.command.PluginCommand;
import org.powernukkitx.command.tree.ParamList;
import org.powernukkitx.command.utils.CommandLogger;
import org.powernukkitx.moneydrop.MoneyDropPlugin;
import java.util.Map;

public class MoneyDropCommand extends PluginCommand<MoneyDropPlugin> {

    public MoneyDropCommand() {
        /*
        1.the name of the command must be lowercase
        2.Here the description is set in with the key in the language file,Look at en_US.lang or zh_CN.lang.
        This can send different command description to players of different language.
        You must extends PluginCommand to have this feature.
        */
        super("examplecommand", "exampleplugin.examplecommand.description", MoneyDropPlugin.INSTANCE);

        //Set the alias for this command
        this.setAliases(new String[]{"test"});

        this.setPermission("exampleplugin.command.examplecommand.say1;exampleplugin.command.examplecommand.say2");
    }


    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var list = result.getValue();
        switch (result.getKey()) {
            case "spawn" -> {
                MoneyDropPlugin.INSTANCE.getLogger().info("spawn custom entity");
                if (sender.isPlayer()) {
                    Player player = sender.asPlayer();
                }
            }
            case "pattern1" -> {
                System.out.println("execute say1");
            }
            case "event" -> {
                int tick = sender.getLocation().getLevel().getTick();
                /*
                 * We call our custom event here
                 */
            }
        }
        //A return of 0 means failure, and a return of 1 means success.
        //This value is applied to the comparator next to the commandblock.
        return 1;
    }
}
