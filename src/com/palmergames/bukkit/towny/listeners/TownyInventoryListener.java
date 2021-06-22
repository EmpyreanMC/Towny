package com.palmergames.bukkit.towny.listeners;

import com.palmergames.bukkit.towny.TownyTech;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.*;
import com.palmergames.bukkit.towny.utils.TechUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.util.Colors;
import org.bukkit.inventory.ItemFlag;

import java.util.Objects;

public class TownyInventoryListener implements Listener {

	public TownyInventoryListener() {

	}

	@EventHandler(ignoreCancelled = true)
	public void onClick(InventoryClickEvent event) {
		
		if (!(event.getInventory().getHolder() instanceof TownyInventory))
			return;
		event.setCancelled(true);
		Player player = (Player) event.getWhoClicked();
		Resident resident = TownyUniverse.getInstance().getResident(player.getUniqueId());
		
		if (resident == null)
			return;
		
		if (event.getCurrentItem() == null) 
			return;
		
		if (event.getInventory().getHolder() instanceof TownyTechInventory) {
			int scroll = ((TownyTechInventory) event.getInventory().getHolder()).scroll;
			// Up
			if (event.getSlot() == 44) {
				TechUtil.openTechGUI(resident, scroll - 1);
			}
			// Down
			if (event.getSlot() == 53) {
				TechUtil.openTechGUI(resident, scroll + 1);
			}
			// Jump to current
			if (event.getSlot() == 8) {
				TechUtil.openTechGUI(resident, -1);
			}
			// Tech
			if (event.getCurrentItem().getItemMeta().hasItemFlag(ItemFlag.HIDE_DESTROYS)) {
				Tech tech = TownyTech.getTechByName(Colors.strip(event.getCurrentItem().getItemMeta().getDisplayName()));
				try {
					Town town = resident.getTown();
					if (Objects.equals(town.getResearchedTech(), tech)) {
						if (!town.isBoosted()) {
							town.boost(resident);
						}
					} else if (town.canResearchTech(tech)) {
						town.startResearchTech(tech);
					}
				} catch (NotRegisteredException ignored) { }
				TechUtil.openTechGUI(resident, scroll);
			}
			return;
		}

		int currentPage = resident.getGUIPageNum();
		
		try {
			// If the pressed item was a nextpage button
			if (event.getCurrentItem().getItemMeta().getDisplayName().equals(Colors.Gold + "Next")) {
				// If there is no next page, don't do anything
				if (resident.getGUIPageNum() >= resident.getGUIPages().size() - 1) {
					return;
				} else {
					// Next page exists, flip the page
					resident.setGUIPageNum(++currentPage);
					new TownyInventory(resident, resident.getGUIPage(), event.getView().getTitle());
				}
				// if the pressed item was a previous page button
			} else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(Colors.Gold + "Back")) {
				// If the page number is more than 0 (So a previous page exists)
				if (resident.getGUIPageNum() > 0) {
					// Flip to previous page
					resident.setGUIPageNum(--currentPage);
					new TownyInventory(resident, resident.getGUIPage(), event.getView().getTitle());
				}
			}
		} catch (Exception ignored) {}	
	}
}