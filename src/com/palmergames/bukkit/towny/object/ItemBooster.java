package com.palmergames.bukkit.towny.object;

import com.palmergames.util.StringMgmt;
import org.apache.commons.lang.WordUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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

	@Override
	public String getFormattedName() {
		String name;
		if (item.getI18NDisplayName() != null) {
			name = item.getI18NDisplayName();
		} else {
			name = WordUtils.capitalizeFully(StringMgmt.remUnderscore(item.getType().toString()));
		}
		return item.getAmount() + " " + name;
	}
}
