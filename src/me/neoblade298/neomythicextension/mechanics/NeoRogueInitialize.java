package me.neoblade298.neomythicextension.mechanics;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.entity.Damageable;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.mobs.ActiveMob;
import me.neoblade298.neorogue.session.Plot;
import me.neoblade298.neorogue.session.Session;
import me.neoblade298.neorogue.session.SessionManager;
import me.neoblade298.neorogue.session.fights.BuffType;
import me.neoblade298.neorogue.session.fights.FightData;
import me.neoblade298.neorogue.session.fights.FightInstance;

public class NeoRogueInitialize implements INoTargetSkill {
	protected HashMap<BuffType, Double> buffs = new HashMap<BuffType, Double>();

    @Override
    public ThreadSafetyLevel getThreadSafetyLevel() {
        return ThreadSafetyLevel.SYNC_ONLY;
    }

	public NeoRogueInitialize(MythicLineConfig config) {
		for (BuffType type : BuffType.values()) {
			double amt = config.getDouble(type.name(), -1);
			if (amt != -1) buffs.put(type, amt);
		}
	}

	@Override
    public SkillResult cast(SkillMetadata data) {
		try {
			SkillCaster caster = data.getCaster();
			Session s = SessionManager.getSession(Plot.locationToPlot(BukkitAdapter.adapt(caster.getLocation())));
			if (s == null) return SkillResult.INVALID_TARGET;
			if (!(caster instanceof ActiveMob)) return SkillResult.INVALID_TARGET;

			ActiveMob mob = (ActiveMob) caster;
			double lvl = 5 + s.getNodesVisited();
			mob.setLevel(lvl);
			double mhealth = mob.getEntity().getMaxHealth();
			mhealth *= lvl / 5;
			mob.getEntity().setMaxHealth(mhealth);
			mob.getEntity().setHealth(mhealth);
			
			UUID uuid = caster.getEntity().getUniqueId();
			FightData fd = new FightData((Damageable) caster.getEntity().getBukkitEntity());
			for (Entry<BuffType, Double> ent : buffs.entrySet()) {
				fd.addBuff(uuid, false, true, ent.getKey(), ent.getValue());
			}
			FightInstance.putFightData(uuid, fd);
			return SkillResult.SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			return SkillResult.ERROR;
		}
    }
}
