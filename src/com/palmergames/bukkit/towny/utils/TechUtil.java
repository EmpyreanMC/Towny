package com.palmergames.bukkit.towny.utils;

import com.palmergames.bukkit.towny.TownyTech;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.*;
import com.palmergames.bukkit.util.Colors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

public class TechUtil {
	
	private enum TechState {
		UNLOCKED,
		RESEARCH_BOOSTED,
		RESEARCH,
		CAN_RESEARCH,
		CAN_CHANGE_RESEARCH,
		NON_PEACEFUL,
		LOCKED
	}
	
	public static void openTechGUI(Resident resident, int scroll) {
		Town town;
		try {
			town = resident.getTown();
		} catch (NotRegisteredException ignored) {
			return;
		}
		
		String name = town.getName() + " Technology Tree";
		Inventory inv = Bukkit.createInventory(null, 54, name);

		boolean isMayor = resident.isMayor();
		Collection<Tech> allTechs = TownyTech.getTechs();
		
		// Get tech to focus on, either current research, the last unlocked tech, or the first tech
		Tech current;
		if (town.getResearchedTech() != null) {
			current = town.getResearchedTech();
		} else if (town.getTechs().size() > 0) {
			current = town.getTechs().get(town.getTechs().size() - 1);
		} else {
			current = allTechs.stream()
				.filter(t -> t.requires.size() == 0)
				.findFirst()
				.orElse(null);
		}
		
		int focus;
		if (scroll == -1) {
			focus = current.guiY;
		} else {
			focus = scroll;
		}
		
		/*
		Scroll clamping
		0
		1 ---- TOP TECH ----
		2 ====== FOCUS ======
		3
		4
		5
		#####################
		6
		7
		8 ====== FOCUS ======
		9
		10 --- BOTTOM TECH ---
		11
		
		Scroll focus position: 3rd row (y: 2)
		 */
		
		// Bottom focus is the bottom tech's Y subtracted by 2 (e.g. 10-2=8)
		// orElse is impossible
		int bottomY = allTechs.stream()
			.mapToInt(t -> t.guiY)
			.max()
			.orElse(4) - 2;
		focus = Math.max(Math.min(focus, bottomY), 2);
		boolean isTop = focus == 2, isBottom = focus == bottomY;

		List<Tech> drawnTechs = new ArrayList<>();
		
		for (Tech tech : allTechs) {
			// Only draw techs within inventory window AND padding rows above and below to make sure all visible roots are drawn
			int padding = 7;
			if (tech.guiY >= focus - 2 - padding || tech.guiY <= focus + 3 + padding) {
				drawnTechs.add(tech);
			}
		}
		
		boolean currentTechVisible = false;
		
		// Draw techs and their roots
		for (Tech tech : drawnTechs) {
			boolean isResearched = Objects.equals(town.getResearchedTech(), tech);
			
			TechState state;
			if (town.hasTech(tech)) {
				state = TechState.UNLOCKED;
			} else if (isResearched) {
				if (town.isBoosted()) {
					state = TechState.RESEARCH_BOOSTED;
				} else {
					state = TechState.RESEARCH;
				}
			} else if (town.canResearchTech(tech)) {
				if (town.getResearchedTech() == null) {
					state = TechState.CAN_RESEARCH;
				} else {
					state = TechState.CAN_CHANGE_RESEARCH;
				}
			// If all requirements are met but tech is non-peaceful
			} else if (tech.nonPeaceful && town.isNeutral()) {
				state = TechState.NON_PEACEFUL;
			} else {
				state = TechState.LOCKED;
			}
			
			int relativeY = tech.guiY - focus + 2,
				techPos = relativeY * 9 + tech.guiX;
			
			if (techPos >= 0 && techPos <= 53) {
				if (tech.equals(current)) {
					currentTechVisible = true;
				}
				ItemStack techItem = new ItemStack(tech.guiIcon);
				ItemMeta meta = techItem.getItemMeta();

				String color;
				List<String> info = new ArrayList<>();
				DecimalFormat df = new DecimalFormat("0.##");
				switch (state) {
					case UNLOCKED:
						color = Colors.DarkPurple;
						info.add(Colors.LightGray + ChatColor.ITALIC + "Unlocked");
						break;
					case RESEARCH_BOOSTED:
						color = Colors.Yellow;
						info.add(Colors.White + "Researching...");
						info.add(Colors.LightGray + (int) Math.ceil((tech.cost - town.getResearch()) / town.getResearchPerHour()) +
							" hours left (" + df.format(town.getResearch()) + "/" + df.format(tech.cost) + ")");
						info.add(Colors.translateColorCodes("&6» &6&oBOOSTED! &6»"));
						break;
					case RESEARCH:
						color = Colors.LightBlue;
						info.add(Colors.White + "Researching...");
						info.add(Colors.LightGray + (int) Math.ceil((tech.cost - town.getResearch()) / town.getResearchPerHour()) +
							" hours left (" + df.format(town.getResearch()) + "/" + df.format(tech.cost) + ")");
						info.add(Colors.Yellow + "Click to boost research!");
						break;
					case CAN_RESEARCH:
						color = Colors.LightGreen;
						if (isMayor) {
							info.add(Colors.White + "Click to begin research!");
							info.add(Colors.LightGray + (int) Math.ceil(tech.cost / town.getResearchPerHour()) +
								" hours (" + df.format(tech.cost) + " research)");
						} else {
							info.add(Colors.White + "We may begin research!");
							info.add(Colors.White + "Ask the mayor to begin it.");
						}
						break;
					case CAN_CHANGE_RESEARCH:
						color = Colors.LightGray;
						if (isMayor) {
							info.add(Colors.Gray + "Click to change current research.");
							info.add(Colors.Red + "This will clear ALL research progress!");
						}
						break;
					case NON_PEACEFUL:
						color = Colors.Rose;
						info.add(Colors.Red + "We must be non-peaceful");
						info.add(Colors.Red + "to research this!");
						break;
					default:
						color = Colors.LightGray;
						info.add(Colors.Gray + ChatColor.ITALIC + "Locked");
						break;
				}
				
				meta.setDisplayName(color + tech.getFormattedName());
				
				List<String> lore = new ArrayList<>();
				if (info.size() > 0) {
					lore.addAll(info);
					lore.add(" ");
				}
				
				if (state == TechState.RESEARCH) {
					lore.add(Colors.LightGray + "Boost: " + town.getCompletedBoosters().size() + "/" + tech.boosts.size());
					lore.addAll(getBoosters(town, tech));
				} else {
					lore.addAll(tech.guiLore.stream().map(Colors::translateColorCodes).collect(Collectors.toList()));
				}
				
				meta.setLore(lore);
				
				meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
				meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
				if (isResearched) {
					meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
					meta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
				}
				
				techItem.setItemMeta(meta);
				inv.setItem(techPos, techItem);
			}
			
			for (Tech.GUIRoot root : tech.guiRoots) {
				int rootPos = (root.y - focus + 2) * 9 + root.x;
				
				if (rootPos >= 0 && rootPos <= 53) {
					ItemStack existingItem = inv.getItem(rootPos);
					
					Material mat;
					switch (state) {
						case UNLOCKED:
							mat = Material.BLUE_STAINED_GLASS_PANE;
							break;
						case RESEARCH_BOOSTED:
							mat = Material.YELLOW_STAINED_GLASS_PANE;
							break;
						case RESEARCH:
							mat = Material.LIGHT_BLUE_STAINED_GLASS_PANE;
							break;
						case CAN_RESEARCH:
							mat = Material.GREEN_STAINED_GLASS_PANE;
							break;
						default:
							mat = Material.GRAY_STAINED_GLASS_PANE;
							break;
					}
					if (existingItem != null &&
						(existingItem.getType() != Material.GRAY_STAINED_GLASS_PANE ||
						mat == Material.GRAY_STAINED_GLASS_PANE)) {
						continue;
					}
					
					ItemStack rootItem = new ItemStack(mat);
					ItemMeta meta = rootItem.getItemMeta();
					meta.setDisplayName(" ");
					rootItem.setItemMeta(meta);
					inv.setItem(rootPos, rootItem);
				}
			}
		}
		
		// Draw overlay buttons
		
		if (!currentTechVisible) {
			boolean isResearched = Objects.equals(town.getResearchedTech(), current);
			
			ItemStack jumpTo = new ItemStack(current.guiIcon);
			ItemMeta meta = jumpTo.getItemMeta();
			meta.setDisplayName(Colors.LightBlue + "Jump to current");
			meta.setLore(Collections.singletonList(Colors.Blue + current.getFormattedName()));
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			
			if (isResearched) {
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				meta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
			}
			
			jumpTo.setItemMeta(meta);
			inv.setItem(8, jumpTo);
		}
		
		ItemStack up = new ItemStack(isTop ? Material.NETHERITE_HELMET : Material.DIAMOND_HELMET);
		ItemMeta meta = up.getItemMeta();
		meta.setDisplayName(Colors.translateColorCodes(isTop ? "&8&l▲ &8Up" : "&b&l▲ &3Up"));
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		up.setItemMeta(meta);

		ItemStack down = new ItemStack(isBottom ? Material.NETHERITE_BOOTS : Material.DIAMOND_BOOTS);
		meta = down.getItemMeta();
		meta.setDisplayName(Colors.translateColorCodes(isBottom ? "&8&l▼ &8Down" : "&b&l▼ &3Down"));
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		down.setItemMeta(meta);
		
		inv.setItem(44, up);
		inv.setItem(53, down);
		
		new TownyTechInventory(resident, inv, name, focus);
	}
	
	private static List<String> getBoosters(Town town, Tech tech) {
		List<Booster> boosters = tech.boosts;
		List<String> lore = new ArrayList<>();
		
		for (Booster booster : boosters) {
			boolean completed = town.getCompletedBoosters().contains(booster);
			lore.add(Colors.translateColorCodes(
				(completed ? "&8" : "&6") + " » " + (completed ? "&8&m" : "&6") + booster.getFormattedName()));
		}
		
		return lore;
	}
}
