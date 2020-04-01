package com.dumbdogdiner.challenges;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.dumbdogdiner.challenges.events.ChallengeListener;
import com.dumbdogdiner.challenges.events.ChallengesGUIListener;
import com.dumbdogdiner.challenges.gui.ChallengesGUI;
import com.dumbdogdiner.challenges.runnables.ChallengeTimeUpdater;
import com.dumbdogdiner.challenges.runnables.TimeChallengesListener;
import com.dumbdogdiner.challenges.utils.Util;

/**
 * The core of the plugin
 */
public class Core extends JavaPlugin {

	public static Core instance;

	public File data;
	public FileConfiguration dataconfig;

	public void onEnable() {
		instance = this;

		File file = new File(getDataFolder(), "config.yml");

		if (!file.exists()) {
			getConfig().options().copyDefaults(true);
			saveDefaultConfig();
		}

		saveConfig();

		Bukkit.getPluginManager().registerEvents(new ChallengeListener(), this);
		Bukkit.getPluginManager().registerEvents(new ChallengesGUIListener(), this);

		for (String key : getConfig().getConfigurationSection("challenges").getKeys(false)) {
			String path = "challenges." + key;
			Challenge challenge = new Challenge(key, ChallengeType.valueOf(getConfig().getString(path + ".type")),
					getConfig().getStringList(path + ".object-types"), new LinkedHashMap<String, Integer>());
			Challenge.challenges.add(challenge);
		}

		registerDataFile();

		// If no challenges were registered from the data file.
		if (ChallengesGUI.challengesInGUI.size() == 0) {
			ChallengesGUI.resetChallengesInGUI();
		}

		new ChallengeTimeUpdater().runTaskTimer(this, 20, 20);
		new TimeChallengesListener().runTaskTimer(this, 20, 20);
	}

	public void onDisable() {
		dataconfig.set("timer.time", Integer.toString(ChallengeTimeUpdater.counter));
		dataconfig.createSection("challengesActive");
		dataconfig.createSection("playerData");

		for (Challenge challenge : ChallengesGUI.challengesInGUI) {
			dataconfig.set("challengesActive." + challenge.getChallengeName(), challenge.getChallengeName());
			for (String playerName : challenge.getCounters().keySet()) {
				dataconfig.set("playerData." + challenge.getChallengeName() + "." + playerName,
						challenge.getCounters().get(playerName));
			}
		}

		saveYML(dataconfig, data);
	}

	public void registerDataFile() {
		data = new File(getDataFolder(), "data.yml");
		dataconfig = YamlConfiguration.loadConfiguration(data);

		if (!dataconfig.contains("challengesActive")) {
			dataconfig.createSection("challengesActive");
			dataconfig.createSection("playerData");
			dataconfig.createSection("timer");

			saveYML(dataconfig, data);
			return;
		}

		if (getTimerTime() == -1) {
			saveYML(dataconfig, data);
			return;
		}

		ChallengeTimeUpdater.setCounter(Integer.parseInt(dataconfig.getString("timer.time")));

		for (String string : dataconfig.getConfigurationSection("challengesActive").getKeys(false)) {
			Challenge challenge = Challenge.getChallengeByName(string);
			LinkedHashMap<String, Integer> counters = new LinkedHashMap<String, Integer>();

			for (String playerName : dataconfig.getConfigurationSection("playerData." + string).getKeys(false)) {
				counters.put(playerName, dataconfig.getInt("playerData." + string + "." + playerName));
			}

			challenge.setCounters(counters);
			ChallengesGUI.challengesInGUI.add(challenge);
		}

		saveYML(dataconfig, data);
	}

	public int getTimerTime() {
		try {
			return Integer.parseInt(dataconfig.getString("timer.time"));
		} catch (Exception e) {
			return -1;
		}
	}

	public void saveYML(FileConfiguration ymlConfig, File ymlFile) {
		try {
			ymlConfig.save(ymlFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Plugin command.
	 */
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "This command can only be run as a player!");
			return true;
		}

		if (!cmd.getName().equalsIgnoreCase("challenges")) {
			return true;
		}

		/**
		 * The player who ran the command.
		 */
		var player = (Player) sender;

		// If no args specified, open the challenge GUI.
		if (args.length == 0) {
			new ChallengesGUI(player);
			return true;
		}

		if (args.length == 1) {
			// "time" - get the remaining time until challenges can be claimed
			if (args[0].equalsIgnoreCase("time")) {
				player.sendMessage(Util.color(getConfig().getString("messages.time-message").replace("%time%",
						Util.timeMessage(ChallengeTimeUpdater.counter))));
				return true;
			}

			if (args[0].equalsIgnoreCase("reset")) {
				if (!player.hasPermission("challenges.admin")) {
					sender.sendMessage(ChatColor.RED + "Missing permissions.");
					return true;
				}

				ChallengeTimeUpdater.counter = 86400;
				ChallengesGUI.resetChallengesInGUI();
				player.sendMessage(Util.color(getConfig().getString("messages.challenges-reset-message")));
				return true;
			}
		}

		sender.sendMessage(ChatColor.RED + "Usage: /challenges [time/reset]");
		return true;
	}
}
