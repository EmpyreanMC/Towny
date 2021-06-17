package com.palmergames.bukkit.towny.tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import com.palmergames.bukkit.config.ConfigNodes;
import com.palmergames.bukkit.towny.object.Tech;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.Bukkit;

import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.TownySettings;
import com.palmergames.bukkit.towny.event.time.NewHourEvent;
import com.palmergames.bukkit.towny.invites.InviteHandler;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.jail.UnJailReason;
import com.palmergames.bukkit.towny.utils.JailUtil;
import com.palmergames.bukkit.towny.war.common.townruin.TownRuinSettings;
import com.palmergames.bukkit.towny.war.common.townruin.TownRuinUtil;

/**
 * This class represents the hourly timer task
 * It is generally set to run once per hour
 * This rate can be configured.
 *
 * @author Goosius
 */
public class HourlyTimerTask extends TownyTimerTask {

	public HourlyTimerTask(Towny plugin) {
		super(plugin);
	}

	@Override
	public void run() {
		if (TownRuinSettings.getTownRuinsEnabled()) {
			TownRuinUtil.evaluateRuinedTownRemovals();
		}
		
		if (TownySettings.getInviteExpirationTime() > 0)
			InviteHandler.searchForExpiredInvites();

		if (!universe.getJailedResidentMap().isEmpty())
			decrementJailedHours();
		
		addTownResearch();
		
		/*
		 * Fire an event other plugins can use.
		 */
		Bukkit.getPluginManager().callEvent(new NewHourEvent(System.currentTimeMillis()));
	}

	/*
	 * Reduce the number of hours jailed residents are jailed for.
	 */
	private void decrementJailedHours() {
		for (Resident resident : new ArrayList<>(universe.getJailedResidentMap()))
			if (resident.hasJailTime())
				if (resident.getJailHours() <= 1)
					Bukkit.getScheduler().runTaskLater(plugin, () -> JailUtil.unJailResident(resident, UnJailReason.SENTENCE_SERVED), 20);
				else {
					resident.setJailHours(resident.getJailHours() - 1);
					resident.save();
				}
	}
	
	private void addTownResearch() {
		List<Town> towns = new ArrayList<>(universe.getDataSource().getTowns());
		ListIterator<Town> townItr = towns.listIterator();
		Town town;

		while (townItr.hasNext()) {
			town = townItr.next();
			/*
			 * Only add research for this town if it really still
			 * exists.
			 * We are running in an Async thread so MUST verify all objects.
			 */
			if (universe.getDataSource().hasTown(town.getName()) && !town.isRuined())
				addTownResearch(town);
		}
	}

	private void addTownResearch(Town town) {
		if (town.getResearchedTech() == null) {
			return;
		}
		
		// https://www.youtube.com/watch?v=_xp3zG-d7w8
		double base = TownySettings.getDouble(ConfigNodes.GTOWN_SETTINGS_BASE_RESEARCH);
		
		for (Tech tech : town.getTechs()) {
			if (tech.researchRate > base) base = tech.researchRate;
		}
		
		town.addResearch(base * (town.isCapital() ? TownySettings.getDouble(ConfigNodes.GTOWN_SETTINGS_CAPITAL_RESEARCH) : 1));
	}
}