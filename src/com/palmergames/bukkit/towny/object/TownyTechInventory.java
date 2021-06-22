package com.palmergames.bukkit.towny.object;

import org.bukkit.inventory.Inventory;

public class TownyTechInventory extends TownyInventory {
	
	public final int scroll;
	
	public TownyTechInventory(Resident res, Inventory inv, String name, int scroll) {
		super(res, inv, name);
		this.scroll = scroll;
	}
}
