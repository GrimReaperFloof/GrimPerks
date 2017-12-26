package me.grimreaperfloof.grimperks;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class Main extends JavaPlugin implements Listener {
	Map<String, Integer> perks = new HashMap<String, Integer>();
	Random random = new Random();
	
	@Override
    public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		
		new BukkitRunnable() {
            @Override
            public void run() {
            	for (String player : perks.keySet()) {
            		if (perks.get(player).equals(1)) {
            			if (Bukkit.getPlayer(player) != null) {
            				Bukkit.getPlayer(player).damage(1);
            			}
            		}
            	}
            }
        }.runTaskTimer(this, 40, 40);
    }
    
	@EventHandler
    public void onPlayerRespawn(org.bukkit.event.player.PlayerRespawnEvent event) {
		perks.remove(event.getPlayer().getName());
		
    	switch (random.nextInt(5)) {
    	case 0:
    		event.getPlayer().sendMessage(ChatColor.AQUA + "You rolled " + ChatColor.GREEN + "God mode" + ChatColor.AQUA + " for 10 seconds!");
    		perks.put(event.getPlayer().getName(), 0);
    		
    		new BukkitRunnable() {
                @Override
                public void run() {
                	if (perks.get(event.getPlayer().getName()).equals(0)) {
                		perks.remove(event.getPlayer().getName());
                	}
                }
            }.runTaskLater(this, 10*20);
            break;
    	case 1:
    		event.getPlayer().sendMessage(ChatColor.AQUA + "You rolled " + ChatColor.RED + "Virus" + ChatColor.AQUA + "!");
    		perks.put(event.getPlayer().getName(), 1);
    		
    		new BukkitRunnable() {
                @Override
                public void run() {
                	event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 2147483647, 0));
                }
            }.runTaskLater(this, 1);
    		break;
    	}
    }
	
	@EventHandler
	public void onPlayerDamage(org.bukkit.event.entity.EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		
		Player player = (Player) event.getEntity();
		if (perks.containsKey(player.getName())) {
			switch (perks.get(player.getName())) {
			case 0:
				event.setCancelled(true);
				break;
			}
		}
	}
	
	public void onPlayerDamageByEntity(org.bukkit.event.entity.EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) {
			return;
		}
		
		Player victim = (Player) event.getEntity();
		Player attacker = (Player) event.getDamager();
		
		if (perks.get(victim.getName()).equals(1) && !perks.get(attacker.getName()).equals(1)) {
			attacker.sendMessage(ChatColor.RED + "You got infected with the virus!");
    		perks.put(attacker.getName(), 1);
    		attacker.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 2147483647, 0));
		} else {
			victim.sendMessage(ChatColor.RED + "You got infected with the virus!");
    		perks.put(victim.getName(), 1);
    		victim.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 2147483647, 0));
		}
	}
}
