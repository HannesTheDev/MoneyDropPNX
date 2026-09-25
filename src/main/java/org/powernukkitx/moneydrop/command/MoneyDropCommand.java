package org.powernukkitx.moneydrop.command;

import net.lldv.llamaeconomy.LlamaEconomy;
import net.lldv.llamaeconomy.components.api.API;
import org.powernukkitx.Player;
import org.powernukkitx.command.CommandSender;
import org.powernukkitx.command.PluginCommand;
import org.powernukkitx.moneydrop.MoneyDropPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MoneyDropCommand extends PluginCommand<MoneyDropPlugin> {

    private final MoneyDropPlugin plugin;
    private final API economy;

    public MoneyDropCommand() {
        super("moneydrop", MoneyDropPlugin.get());

        this.plugin = MoneyDropPlugin.get();
        this.economy = LlamaEconomy.getAPI();

        this.setDescription("Make a money drop");
        this.setPermission("moneydrop.command");
        this.setUsage("§c/moneydrop [number]");
        this.setAliases(new String[]{"md","moneyd", "mdrop"});
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {

        String prefix = "§8[§6§lMetroVerse§r§8] §r§8» §r";
        if (!sender.isPlayer()) {
            sender.sendMessage(prefix + plugin.getMessage("no_player"));
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {

            double balance = economy.getMoney(player);

            sender.sendMessage(prefix + plugin.getMessage("usage").replace("{balance}", format(balance)));
            return true;
        }

        double amount;

        try {
            amount = Double.parseDouble(args[0]);
        } catch (NumberFormatException exception) {

            sender.sendMessage(prefix + plugin.getMessage("invalid_amount"));
            return true;
        }

        if (amount <= 0) {

            sender.sendMessage(prefix + plugin.getMessage("invalid_amount"));
            return true;
        }

        if (amount < 5000) {
            sender.sendMessage(prefix + plugin.getMessage("minimum_amount"));
            return true;
        }


        double senderMoney = economy.getMoney(player);
        if (senderMoney < amount) {
            double missing = amount - senderMoney;
            sender.sendMessage(plugin.getMessage(prefix + "not_enough_money").replace("{missing}", format(missing)));
            return true;
        }

        Map<UUID, Player> onlinePlayers = plugin.getServer().getOnlinePlayers();

        if (onlinePlayers.size() < 5) {
            sender.sendMessage(prefix + plugin.getMessage("too_few_players"));
            return true;
        }

        List<Player> receivers = new ArrayList<>();
        for (Player online : onlinePlayers.values()) {
            if (online.equals(player)) {
                continue;
            }

            if (online.isOp()) {
                continue;
            }

            receivers.add(online);
        }

        if (receivers.isEmpty()) {
            sender.sendMessage(plugin.getMessage("too_few_players"));
            return true;
        }

        double moneyPerPlayer = amount / receivers.size();

        economy.reduceMoney(player, amount);

        for (Player receiver : receivers) {
            economy.addMoney(receiver, moneyPerPlayer);
        }


        List<String> names = new ArrayList<>();
        for (Player receiver : receivers) {
            names.add(receiver.getName());
        }

        String playerNames = String.join(", ", names);


        String header = prefix + plugin.getMessage("drop_header");

        String players = prefix + plugin.getMessage("drop_players").replace("{players}", playerNames);

        String receive = prefix + plugin.getMessage("drop_receive").replace("{amount}", format(moneyPerPlayer));

        String thanks = prefix + plugin.getMessage("drop_thanks")
                .replace("{player}", player.getName())
                        .replace("{amount}", format(amount));


        plugin.getServer().broadcastMessage(header
                        + "\n"
                        + players
                        + "\n"
                        + receive
                        + "\n"
                        + thanks
        );
        return true;
    }

    private String format(double amount) {

        if (amount == Math.floor(amount)) {
            return String.valueOf(
                    (long) amount
            );
        }

        return String.valueOf(amount);
    }
}