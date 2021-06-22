package com.palmergames.bukkit.towny.object;

import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class SlimefunBooster extends Booster {
	
	private final SlimefunItem sfItem;
	private final int qty;

	public SlimefunBooster(SlimefunItem sfItem, int qty) {
		this.sfItem = sfItem;
		this.qty = qty;
	}

	@Override
	public boolean boost(Resident resident) {
		Player player = resident.getPlayer();
		if (player != null && canBoost(resident)) {
			ItemStack item = sfItem.getItem();
			item.setAmount(qty);
			player.getInventory().removeItem(item);
			return true;
		}
		return false;
	}

	@Override
	public boolean canBoost(Resident resident) {
		Player player = resident.getPlayer();
		return player != null && player.getInventory().containsAtLeast(sfItem.getItem(), qty);
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (o == null)
			return false;
		if (!(o instanceof SlimefunBooster))
			return false;
		SlimefunBooster other = (SlimefunBooster) o;
		return Objects.equals(sfItem, other.sfItem) &&
			Objects.equals(qty, other.qty);
	}

	@Override
	public String getName() {
		return "SLIMEFUN:" + sfItem.getId() + " " + qty;
	}

	@Override
	public String getFormattedName() {
		return qty + " " + sfItem.getItemName();
	}
}
