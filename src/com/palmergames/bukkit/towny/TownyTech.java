package com.palmergames.bukkit.towny;

import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Booster;
import com.palmergames.bukkit.towny.object.ItemBooster;
import com.palmergames.bukkit.towny.object.SlimefunBooster;
import com.palmergames.bukkit.towny.object.Tech;
import com.palmergames.util.FileMgmt;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class TownyTech {
	
	private static Map<String, Tech> techs;
	
	public static Tech getTech(String techName) {
		return techs.get(techName);
	}

    public static void loadTechs(String filepath, String defaultRes) throws TownyException {
		String fullPath = filepath + File.separator + defaultRes;
		File file = FileMgmt.unpackResourceFile(fullPath, defaultRes, defaultRes);

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

		techs = getTechs(config);
    }

    private static Map<String, Tech> getTechs(@NotNull FileConfiguration config) {
    	Map<String, Tech> cache = new HashMap<>();
    	
    	for (String key : config.getKeys(false)) {
    		getTech(config, cache, key);
    	}
    	
    	return cache;
    }
    
    @Nullable
	private static Tech getTech(@NotNull FileConfiguration config, Map<String, Tech> cache, String id) {
		if (cache.containsKey(id)) return cache.get(id);
		
		ConfigurationSection section = config.getConfigurationSection(id);

		if (section == null) return null;

		Map<String, List<String>> rankPerms = new HashMap<>();
		ConfigurationSection rankPermsSection = section.getConfigurationSection("permissions.ranks");

		if (rankPermsSection != null) {
			rankPermsSection.getKeys(false).forEach(k -> rankPerms.put(k, rankPermsSection.getStringList(k)));
		}

		List<Tech> requires = section.getStringList("requires")
			.stream()
			.map(key -> getTech(config, cache, key))
			.collect(Collectors.toList());

		Tech tech = new Tech(
			id,
			section.getString("name"),
			section.getInt("x-location"),
			section.getInt("max-residents"),
			section.getInt("innovation-slots"),
			section.getDouble("cost"),
			section.getDouble("research-rate"),
			section.getBoolean("non-peaceful"),
			Material.matchMaterial(Objects.requireNonNull(section.getString("icon"))),
			section.getStringList("lore"),
			section.getStringList("permissions.default"),
			section.getStringList("permissions.mayor"),
			rankPerms,
			parseBoost(section.getStringList("boost")),
			requires
		);
		
		cache.put(id, tech);
		return tech;
    }
    
    private static List<Booster> parseBoost(List<String> boosters) {
    	return boosters.stream().map(Booster::fromString).collect(Collectors.toList());
	}
}
