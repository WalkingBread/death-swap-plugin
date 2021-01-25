package pl.walkingbread.deathswap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Main extends JavaPlugin implements Listener {
	public List<Player> players = new ArrayList<>();
	public boolean inGame = false;

	@Override
	public void onEnable() {
		initGame(300);

		Bukkit.getPluginManager().registerEvents(this, this);
	}

	public void deathSwap() {
		Location last_0 = null;
		for (int i = 0; i < players.size(); i++) {
			Player p = null;
			Player p2 = null;
			Location location = null;
			if (i == players.size() - 1) {
				p = players.get(i);
				location = last_0;
			} else {
				p = players.get(i);
				p2 = players.get(i + 1);

				if (i == 0) {
					last_0 = p.getLocation();
				}
				location = p2.getLocation();
			}

			p.teleport(location);
		}
	}

	public void stopGame() {
		getServer().broadcastMessage("The game has been stopped.");
		players = new ArrayList<>();
		inGame = false;
	}

	public void initGame(int round_time) {

		getServer().getScheduler().runTaskTimer(this, new Runnable() {
			int time = round_time;

			@Override
			public void run() {
				if (inGame) {

					if (players.size() == 1) {
						Player winner = players.get(0);
						getServer().broadcastMessage(winner.getDisplayName() + " is the winner.");
						stopGame();
					}

					if (this.time == 0) {
						getServer().broadcastMessage("Death swap!");
						deathSwap();

						time = round_time;
					} else if (this.time <= 5) {
						getServer().broadcastMessage(this.time + " second(s) remains!");
					} else if (this.time % 10 == 0) {
						getServer().broadcastMessage(this.time + " second(s) remains!");
					}

					this.time--;
				}
			}
		}, 0L, 20L);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equalsIgnoreCase("deathswap") && sender instanceof Player) {
			Player _sender = (Player) sender;

			if (inGame) {
				_sender.sendMessage("The game has already started.");
			} else {

				if(args.length == 0) {
					if (getServer().getOnlinePlayers().size() > 1) {
						for (Player p : getServer().getOnlinePlayers()) {
							players.add(p);
							inGame = true;
						}
					} else {
						_sender.sendMessage("You can\'t play alone dumbass.");
					}
				} else if(args.length == 1) {
					_sender.sendMessage("You can\'t play alone dumbass.");

				} else if(args.length > 1) {
					for (String s : args) {
						Player p = getServer().getPlayer(s);
						players.add(p);
					}
					inGame = true;
				}
			}

		} else if(command.getName().equalsIgnoreCase("stopgame") && sender instanceof Player) {
			Player _sender = (Player) sender;
			if(inGame) {
				stopGame();
			} else {
				_sender.sendMessage("The game has not started yet.");
			}
		}

		return super.onCommand(sender, command, label, args);
	}

	@EventHandler
	public void onPlayerDead (PlayerDeathEvent e) {
		if (e.getEntity() instanceof  Player) {

			Player p = (Player) e.getEntity();
			if (players.contains(p) && inGame) {
				players.remove(p);

				getServer().broadcastMessage(p.getDisplayName() + " got out of the game.");
			}
		}
	}

}
