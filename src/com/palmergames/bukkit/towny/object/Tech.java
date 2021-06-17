package com.palmergames.bukkit.towny.object;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class Tech implements Nameable {
	
	@NotNull
	public final String id;
	public final String name;
	public final int xLocation, maxResidents, innovationSlots;
	public final double cost, researchRate;
	public final boolean nonPeaceful;
	public final Material icon;
	public final List<String> lore, defaultPerms, mayorPerms;
	public final Map<String, List<String>> rankPerms;
	public final List<Booster> boosts;
	public final List<Tech> requires;

	public Tech(@NotNull String id, String name, int xLocation, int maxResidents, int innovationSlots, double cost, double researchRate, boolean nonPeaceful, Material icon, List<String> lore, List<String> defaultPerms, List<String> mayorPerms, Map<String, List<String>> rankPerms, List<Booster> boosts, List<Tech> requires) {
		this.id = id;
		this.name = name;
		this.xLocation = xLocation;
		this.maxResidents = maxResidents;
		this.innovationSlots = innovationSlots;
		this.cost = cost;
		this.researchRate = researchRate;
		this.nonPeaceful = nonPeaceful;
		this.icon = icon;
		this.lore = lore;
		this.defaultPerms = defaultPerms;
		this.mayorPerms = mayorPerms;
		this.rankPerms = rankPerms;
		this.boosts = boosts;
		this.requires = requires;
	}

	@Override
	public String getName() {
		return id;
	}
	
	@Override
	public String toString() {
		return getName();
	}
}
