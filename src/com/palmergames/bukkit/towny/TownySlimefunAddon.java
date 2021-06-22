package com.palmergames.bukkit.towny;

import com.palmergames.bukkit.towny.multiblocks.Academy;
import com.palmergames.bukkit.towny.multiblocks.ResearchTable;
import com.palmergames.bukkit.towny.multiblocks.Studies;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public class TownySlimefunAddon implements SlimefunAddon {

	public TownySlimefunAddon() {
		registerItems();
	}

	@Override
	public JavaPlugin getJavaPlugin() {
		return Towny.getPlugin();
	}

	@Override
	public String getBugTrackerURL() {
		return null;
	}
	
	public void registerItems() {
		Category techCategory = new Category(
			new NamespacedKey(getJavaPlugin(), "technology"),
			new CustomItem(Material.WRITABLE_BOOK, "&dTechnology")
		);
		new ResearchTable(techCategory, new SlimefunItemStack(
			"RESEARCH_TABLE",
			Material.ENCHANTING_TABLE,
			"&bResearch Table",
			"",
			"&aUnlocks new Technologies in your town"
		)).register(this);
		new Studies(techCategory, new SlimefunItemStack(
			"STUDIES",
			Material.LECTERN,
			"&bStudies",
			"",
			"&aFunds your Research to speed up progress"
		)).register(this);
		new Academy(techCategory, new SlimefunItemStack(
			"ACADEMY",
			Material.BREWING_STAND,
			"&bAcademy",
			"",
			"&aCreates new Innovations with Research"
		)).register(this);
	}
}
