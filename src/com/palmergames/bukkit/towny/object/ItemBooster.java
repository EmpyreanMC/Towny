package com.palmergames.bukkit.towny.object;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemBooster implements Booster {
	
	private final ItemStack item;

	public ItemBooster(ItemStack material) {
		this.item = material;
	}

	@Override
	public boolean boost(Player player) {
		if (canBoost(player)) {
			player.getInventory().removeItem(item);
			return true;
		}
		return false;
	}

	@Override
	public boolean canBoost(Player player) {
		return player.getInventory().containsAtLeast(item, item.getAmount());
	}
}
