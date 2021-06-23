package com.palmergames.bukkit.towny.object;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Tech implements Nameable {

	public static final class GUIRoot {
		public final int x, y;

		public GUIRoot(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public static GUIRoot fromString(String string) {
			String[] split = string.split(",");
			return new GUIRoot(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
		}
	}
	
	@NotNull
	public final String id;
	public final String name;
	public final int guiX, guiY, maxResidents, innovationSlots;
	public final double cost, researchRate;
	public final boolean nonPeaceful;
	public final Material guiIcon;
	public final List<String> guiLore, defaultPerms, mayorPerms;
	public final Map<String, List<String>> rankPerms;
	public final List<Booster> boosts;
	public final List<Tech> requires;
	public final List<GUIRoot> guiRoots;

	public Tech(@NotNull String id, String name, int guiX, int guiY, int maxResidents, int innovationSlots, double cost, double researchRate, boolean nonPeaceful, Material guiIcon, List<String> guiLore, List<String> defaultPerms, List<String> mayorPerms, Map<String, List<String>> rankPerms, List<Booster> boosts, List<Tech> requires, List<GUIRoot> guiRoots) {
		this.id = id;
		this.name = name;
		this.guiX = guiX;
		this.guiY = guiY;
		this.maxResidents = maxResidents;
		this.innovationSlots = innovationSlots;
		this.cost = cost;
		this.researchRate = researchRate;
		this.nonPeaceful = nonPeaceful;
		this.guiIcon = guiIcon;
		this.guiLore = guiLore;
		this.defaultPerms = defaultPerms;
		this.mayorPerms = mayorPerms;
		this.rankPerms = rankPerms;
		this.boosts = boosts;
		this.requires = requires;
		this.guiRoots = guiRoots;
	}

	@Override
	public String getName() {
		return id;
	}

	@Override
	public String getFormattedName() {
		return name;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (o == null)
			return false;
		if (!(o instanceof Tech))
			return false;
		Tech other = (Tech) o;
		return Objects.equals(id, other.id);
	}
}
