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

        this.commandParameters.clear();
    }

    @Override
    public boolean execute(
            CommandSender sender,
            String commandLabel,
            String[] args
    ) {

        /*
         * Nur Spieler dürfen /moneydrop benutzen
         */
        String prefix = "§8[§6§lMetroVerse§r§8] §r§8» §r";
        if (!sender.isPlayer()) {
            sender.sendMessage(prefix + plugin.getMessage("no_player"));
            return true;
        }

        Player player = (Player) sender;

        /*
         * Kein Betrag angegeben
         */
        if (args.length == 0) {

            double balance = economy.getMoney(player);

            sender.sendMessage(prefix + plugin.getMessage("usage"));
            return true;
        }

        /*
         * Betrag aus Argument lesen
         */
        double amount;

        try {
            amount = Double.parseDouble(args[0]);
        } catch (NumberFormatException exception) {

            sender.sendMessage(prefix + plugin.getMessage("invalid_amount"));
            return true;
        }

        /*
         * Keine negativen oder 0 Coins
         */
        if (amount <= 0) {

            sender.sendMessage(prefix + plugin.getMessage("invalid_amount"));
            return true;
        }

        /*
         * Mindestbetrag: 5000 Coins
         */
        if (amount < 5000) {
            sender.sendMessage(prefix + plugin.getMessage("minimum_amount"));
            return true;
        }

        /*
         * Kontostand überprüfen
         */
        double senderMoney = economy.getMoney(player);
        if (senderMoney < amount) {
            double missing = amount - senderMoney;
            sender.sendMessage(plugin.getMessage(prefix + "not_enough_money").replace("{missing}", format(missing)));
            return true;
        }

        /*
         * Alle Online-Spieler holen.
         *
         * PNX 3.0.0 gibt hier eine
         * Map<UUID, Player> zurück.
         */
        Map<UUID, Player> onlinePlayers = plugin.getServer().getOnlinePlayers();

        /*
         * Mindestens 5 Spieler online
         */
        if (onlinePlayers.size() < 5) {
            sender.sendMessage(prefix + plugin.getMessage("too_few_players"));
            return true;
        }

        /*
         * Empfänger bestimmen.
         *
         * Der Ersteller und OPs bekommen kein Geld.
         */
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

        /*
         * Keine Empfänger vorhanden
         */
        if (receivers.isEmpty()) {
            sender.sendMessage(plugin.getMessage("too_few_players"));
            return true;
        }

        /*
         * Geld gleichmäßig verteilen
         */
        double moneyPerPlayer = amount / receivers.size();

        /*
         * Geld vom Ersteller abziehen
         */
        economy.reduceMoney(player, amount);

        /*
         * Geld an Empfänger verteilen
         */
        for (Player receiver : receivers) {
            economy.addMoney(receiver, moneyPerPlayer);
        }

        /*
         * Namen der Empfänger sammeln
         */
        List<String> names = new ArrayList<>();
        for (Player receiver : receivers) {
            names.add(receiver.getName());
        }

        String playerNames = String.join(", ", names);

        /*
         * Nachrichten
         */

        String header = prefix + plugin.getMessage("drop_header");

        String players = prefix + plugin.getMessage("drop_players").replace("{players}", playerNames);

        String receive = prefix + plugin.getMessage("drop_receive").replace("{amount}", format(moneyPerPlayer));

        String thanks = prefix + plugin.getMessage("drop_thanks")
                .replace("{player}", player.getName())
                        .replace("{amount}", format(amount));

        /*
         * Broadcast an alle Spieler
         */
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

    /**
     * Formatiert Zahlen:
     *
     * 5000.0 -> 5000
     * 1250.5 -> 1250.5
     */
    private String format(double amount) {

        if (amount == Math.floor(amount)) {
            return String.valueOf(
                    (long) amount
            );
        }

        return String.valueOf(amount);
    }
}