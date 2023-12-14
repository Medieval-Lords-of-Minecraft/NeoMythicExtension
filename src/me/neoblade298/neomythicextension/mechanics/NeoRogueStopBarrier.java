package me.neoblade298.neomythicextension.mechanics;

import org.bukkit.entity.Entity;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import me.neoblade298.neorogue.session.fight.FightData;
import me.neoblade298.neorogue.session.fight.FightInstance;

public class NeoRogueStopBarrier implements ITargetedEntitySkill {
	protected final String id;

    @Override
    public ThreadSafetyLevel getThreadSafetyLevel() {
        return ThreadSafetyLevel.SYNC_ONLY;
    }

	public NeoRogueStopBarrier(MythicLineConfig config) {
		id = config.getString("id");
	}

	@Override
    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
		try {
			Entity owner = target.getBukkitEntity();
			FightData fd = FightInstance.getFightData(owner.getUniqueId());
			if (fd == null) return SkillResult.INVALID_TARGET;
			fd.getInstance().removeEnemyBarrier(NeoRogueBarrier.barrierIds.get(id));
			return SkillResult.SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			return SkillResult.ERROR;
		}
    }
}
