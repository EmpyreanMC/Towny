package com.palmergames.bukkit.towny;

import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Booster;
import com.palmergames.bukkit.towny.object.Tech;
import com.palmergames.util.FileMgmt;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class TownyTech {
	
	private static Map<String, Tech> techs;
	
	public static Tech getTech(String id) {
		return techs.get(id);
	}
	
	public static Tech getTechByName(String name) {
		return techs.values().stream().filter(t -> t.name.equals(name)).findFirst().orElse(null);
	}

	public static Collection<Tech> getTechs() {
		return techs.values();
	}

	public static void loadTechs(String filepath, String defaultRes) throws TownyException {
		String fullPath = filepath + File.separator + defaultRes;
		File file = FileMgmt.unpackResourceFile(fullPath, defaultRes, defaultRes);

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

		techs = getTechs(config);
    }

    private static Map<String, Tech> getTechs(@NotNull FileConfiguration config) throws TownyException {
    	Map<String, Tech> cache = new HashMap<>();
    	
    	for (String key : config.getKeys(false)) {
    		getTech(config, cache, key);
    	}
    	
    	return cache;
    }
    
    @Nullable
	private static Tech getTech(@NotNull FileConfiguration config, Map<String, Tech> cache, String id) throws TownyException {
		if (cache.containsKey(id)) return cache.get(id);
		
		ConfigurationSection section = config.getConfigurationSection(id);

		if (section == null) return null;

		Map<String, List<String>> rankPerms = new HashMap<>();
		ConfigurationSection rankPermsSection = section.getConfigurationSection("permissions.ranks");

		if (rankPermsSection != null) {
			rankPermsSection.getKeys(false).forEach(k -> rankPerms.put(k, rankPermsSection.getStringList(k)));
		}

		List<Tech> requires = new ArrayList<>();
		for (String key : section.getStringList("requires")) {
			requires.add(getTech(config, cache, key));
		}

		Tech tech = new Tech(
			id,
			section.getString("name"),
			section.getInt("gui.x"),
			section.getInt("gui.y"),
			section.getInt("max-residents"),
			section.getInt("innovation-slots"),
			section.getDouble("cost"),
			section.getDouble("research-rate"),
			section.getBoolean("non-peaceful"),
			Material.matchMaterial(Objects.requireNonNull(section.getString("gui.icon"))),
			section.getStringList("gui.lore"),
			section.getStringList("permissions.default"),
			section.getStringList("permissions.mayor"),
			rankPerms,
			parseBoost(section.getStringList("boost")),
			requires,
			section.getStringList("gui.roots").stream().map(Tech.GUIRoot::fromString).collect(Collectors.toList()));
		
		cache.put(id, tech);
		return tech;
    }
    
    private static List<Booster> parseBoost(List<String> boosters) throws TownyException {
		List<Booster> result = new ArrayList<>();
		for (String booster : boosters) {
			result.add(Booster.fromString(booster));
		}
		return result;
	}
}
