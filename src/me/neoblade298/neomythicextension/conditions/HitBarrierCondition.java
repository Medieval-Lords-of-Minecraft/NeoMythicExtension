package me.neoblade298.neomythicextension.conditions;

import java.util.HashSet;

import org.bukkit.Location;
import org.bukkit.Sound;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillTrigger;
import io.lumine.mythic.api.skills.conditions.ISkillMetaComparisonCondition;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.skills.mechanics.ProjectileMechanic.ProjectileMechanicTracker;
import me.neoblade298.neorogue.equipment.mechanics.Barrier;
import me.neoblade298.neorogue.session.fights.FightData;
import me.neoblade298.neorogue.session.fights.FightInstance;

public class HitBarrierCondition implements ISkillMetaComparisonCondition {
	protected Skill skill;

	public HitBarrierCondition(MythicLineConfig mlc) {
		String skillName = mlc.getString(new String[] { "onHit", "oH" });
		if (skillName != null) {
			skill = MythicBukkit.inst().getSkillManager().getSkill(skillName).get();
		}
	}

	@Override
	public boolean check(SkillMetadata data, AbstractEntity ent) {
		ProjectileMechanicTracker tracker = (ProjectileMechanicTracker) data.getCallingEvent();
		Location loc = BukkitAdapter.adapt(tracker.getCurrentLocation());
		FightData barrierOwner = null;
		for (FightData fd : FightInstance.getUserData().values()) {
			Barrier b = fd.getBarrier();
			if (b == null) continue;
			
			if (b.collides(loc)) {
				tracker.projectileEnd();
				tracker.setCancelled();
				loc.getWorld().playSound(loc, Sound.ITEM_SHIELD_BLOCK, 1F, 1F);
				barrierOwner = fd;
			}
			
			if (barrierOwner == null) {
				continue;
			}
			
			HashSet<AbstractEntity> targets = new HashSet<AbstractEntity>();
			targets.add(BukkitAdapter.adapt(barrierOwner.getPlayer()));
			
			if (skill == null) return false;
			skill.execute(SkillTrigger.get("API"), data.getCaster(), data.getTrigger(),
					data.getCaster().getLocation(), targets, null, 1F);
			return false;
		}
		return true;
	}
}
