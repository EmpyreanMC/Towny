package com.palmergames.bukkit.towny.object;

import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SlimefunBooster implements Booster {
	
	private final SlimefunItem sfItem;
	private final int qty;

	public SlimefunBooster(SlimefunItem sfItem, int qty) {
		this.sfItem = sfItem;
		this.qty = qty;
	}

	@Override
	public boolean boost(Player player) {
		if (canBoost(player)) {
			ItemStack item = sfItem.getItem();
			item.setAmount(qty);
			player.getInventory().removeItem(item);
			return true;
		}
		return false;
	}

	@Override
	public boolean canBoost(Player player) {
		return player.getInventory().containsAtLeast(sfItem.getItem(), qty);
	}
}
