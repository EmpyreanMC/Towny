package com.palmergames.bukkit.towny.multiblocks;

import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.TownyTech;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.utils.ResidentUtil;
import com.palmergames.bukkit.towny.utils.TechUtil;
import com.palmergames.bukkit.util.Colors;
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
			TechUtil.openTechGUI(resident, -1);
		} else {
			TownyMessaging.sendMessage(player, Colors.Rose + "You have to be in a town to use a Research Table!");
		}
    }
}