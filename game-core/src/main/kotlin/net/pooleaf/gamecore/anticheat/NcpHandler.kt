package net.pooleaf.gamecore.anticheat

import fr.neatmonster.nocheatplus.checks.CheckType
import fr.neatmonster.nocheatplus.hooks.NCPExemptionManager
import net.pooleaf.gamecore.anticheat.AntiCheatCheckType.*
import org.bukkit.entity.Player

class NcpHandler : AntiCheatHandler {

    override fun exempt(player: Player, checkType: AntiCheatCheckType) {
        when (checkType) {
            FIGHT -> {
                NCPExemptionManager.exemptPermanently(player, CheckType.FIGHT_ANGLE)
                NCPExemptionManager.exemptPermanently(player, CheckType.FIGHT_DIRECTION)
                NCPExemptionManager.exemptPermanently(player, CheckType.FIGHT_NOSWING)
                NCPExemptionManager.exemptPermanently(player, CheckType.FIGHT_REACH)
                NCPExemptionManager.exemptPermanently(player, CheckType.FIGHT_SELFHIT)
                NCPExemptionManager.exemptPermanently(player, CheckType.FIGHT_SPEED)
            }
        }
    }

    override fun unexempt(player: Player, checkType: AntiCheatCheckType) {
        when (checkType) {
            FIGHT -> {
                NCPExemptionManager.unexempt(player, CheckType.FIGHT_ANGLE)
                NCPExemptionManager.unexempt(player, CheckType.FIGHT_DIRECTION)
                NCPExemptionManager.unexempt(player, CheckType.FIGHT_NOSWING)
                NCPExemptionManager.unexempt(player, CheckType.FIGHT_REACH)
                NCPExemptionManager.unexempt(player, CheckType.FIGHT_SELFHIT)
                NCPExemptionManager.unexempt(player, CheckType.FIGHT_SPEED)
            }
        }
    }

}