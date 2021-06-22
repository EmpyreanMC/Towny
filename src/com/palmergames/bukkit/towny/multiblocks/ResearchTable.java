package com.palmergames.bukkit.towny.multiblocks;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.Translation;
import com.palmergames.bukkit.towny.utils.TechUtil;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlockMachine;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ResearchTable extends MultiBlockMachine {
    public ResearchTable(Category category, SlimefunItemStack item) {
        super(category, item, new ItemStack[] {
                null, null, null,
                null, null, null,
                new ItemStack(Material.LAPIS_BLOCK), new ItemStack(Material.ENCHANTING_TABLE), new ItemStack(Material.LAPIS_BLOCK)
        }, BlockFace.UP);
    }

    @Override
    public void onInteract(Player player, Block block) {
		Resident resident = TownyUniverse.getInstance().getResident(player.getUniqueId());
		
		if (resident == null) return;
		
		if (resident.hasTown()) {
			Town town = TownyAPI.getInstance().getTown(block.getLocation());
			if (town != null && town.hasResident(resident)) {
				TechUtil.openTechGUI(resident, -1);
			} else {
				TownyMessaging.sendMessage(player, Translation.of("msg_research_table_outside_town"));
			}
		} else {
			TownyMessaging.sendMessage(player, Translation.of("msg_research_table_without_town"));
		}
    }
}