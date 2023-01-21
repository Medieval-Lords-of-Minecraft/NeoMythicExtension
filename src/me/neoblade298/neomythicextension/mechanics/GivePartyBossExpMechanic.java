package me.neoblade298.neomythicextension.mechanics;

import org.bukkit.entity.Player;

import com.alessiodp.parties.api.Parties;
import com.alessiodp.parties.api.interfaces.Party;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import me.neoblade298.neocore.bukkit.info.InfoAPI;

public class GivePartyBossExpMechanic implements ITargetedEntitySkill {

	protected final String boss;

    @Override
    public ThreadSafetyLevel getThreadSafetyLevel() {
        return ThreadSafetyLevel.SYNC_ONLY;
    }

	public GivePartyBossExpMechanic(MythicLineConfig config) {
        this.boss = config.getString(new String[] {"b", "boss"}, "Ratface");
	}
	
	@Override
    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
		try {
			if (target.getBukkitEntity() instanceof Player) {
				Player p = (Player) target.getBukkitEntity();
				int exp = InfoAPI.getBossInfo(this.boss).getLevel(false);
				Party party = Parties.getApi().getPartyOfPlayer(p.getUniqueId());
				if (party != null) {
					party.giveExperience(exp);
				}
				return SkillResult.SUCCESS;
			}
			return SkillResult.INVALID_TARGET;
		} catch (Exception e) {
			e.printStackTrace();
			return SkillResult.ERROR;
		}
    }
}
