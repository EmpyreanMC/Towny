package com.palmergames.bukkit.towny.object;

import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public abstract class Booster implements Nameable {
	
	public abstract boolean boost(Resident resident);
	
	public abstract boolean canBoost(Resident resident);

	@Override
	public abstract boolean equals(Object obj);

	@Override
	public abstract String getName();

	@Override
	public abstract String getFormattedName();

	@Override
	public String toString() {
		return getName();
	}
	
	public static Booster fromString(String string) throws TownyException {
		String[] split = string.split(" ");

		String item = split[0];
		int qty = Integer.parseInt(split[1]);

		if (item.startsWith("SLIMEFUN:")) {
			if (Towny.getPlugin().getServer().getPluginManager().isPluginEnabled("Slimefun")) {
				SlimefunItem sfItem = SlimefunItem.getByID(item.substring(9));

				if (sfItem != null) {
					return new SlimefunBooster(sfItem, qty);
				}
			}
		} else {
			Material material = Material.matchMaterial(item);

			if (material != null) {
				return new ItemBooster(new ItemStack(material, qty));
			}
		}
		
		throw new TownyException("Invalid booster: " + string);
	}
}
