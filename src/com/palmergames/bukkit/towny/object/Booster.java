package com.palmergames.bukkit.towny.object;

import org.bukkit.entity.Player;

public interface Booster {
	
	boolean boost(Player player);
	
	boolean canBoost(Player player);
}
