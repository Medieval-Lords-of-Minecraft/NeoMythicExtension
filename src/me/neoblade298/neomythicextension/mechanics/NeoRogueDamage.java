package me.neoblade298.neomythicextension.mechanics;

import org.bukkit.entity.Damageable;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import me.neoblade298.neorogue.session.fights.DamageType;
import me.neoblade298.neorogue.session.fights.FightInstance;

public class NeoRogueDamage implements ITargetedEntitySkill {
	protected final int amount;
	protected final DamageType type;
	protected final boolean hitBarrier;

    @Override
    public ThreadSafetyLevel getThreadSafetyLevel() {
        return ThreadSafetyLevel.SYNC_ONLY;
    }

	public NeoRogueDamage(MythicLineConfig config) {
        this.amount = config.getInteger(new String[] { "a", "amount" }, 0);
        this.type = DamageType.valueOf(config.getString(new String[] {"t", "type"}, "BLUNT").toUpperCase());
        this.hitBarrier = config.getBoolean(new String[] { "hb", "hitbarrier" }, false);
	}

	@Override
    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
		try {
			double level = data.getCaster().getLevel();
			final double fAmount = level > 5 ? amount * (level / 5) : amount;
			if (hitBarrier) FightInstance.dealBarrieredDamage((Damageable) data.getCaster().getEntity().getBukkitEntity(), type, fAmount, (Damageable) target.getBukkitEntity());
			else FightInstance.dealDamage((Damageable) data.getCaster().getEntity().getBukkitEntity(), type, fAmount, (Damageable) target.getBukkitEntity());
			return SkillResult.SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			return SkillResult.ERROR;
		}
    }
}