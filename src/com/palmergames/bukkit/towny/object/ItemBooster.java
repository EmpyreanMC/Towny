package com.palmergames.bukkit.towny.object;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class ItemBooster extends Booster {
	
	private final ItemStack item;

	public ItemBooster(ItemStack item) {
		this.item = item;
	}

	@Override
	public boolean boost(Resident resident) {
		Player player = resident.getPlayer();
		if (player != null && canBoost(resident)) {
			player.getInventory().removeItem(item);
			return true;
		}
		return false;
	}

	@Override
	public boolean canBoost(Resident resident) {
		Player player = resident.getPlayer();
		return player != null && player.getInventory().containsAtLeast(item, item.getAmount());
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (o == null)
			return false;
		if (!(o instanceof ItemBooster))
			return false;
		ItemBooster other = (ItemBooster) o;
		return Objects.equals(item, other.item);
	}

	@Override
	public String getName() {
		return item.getType() + " " + item.getAmount();
	}
}
