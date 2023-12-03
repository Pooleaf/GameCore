package net.pooleaf.gamecore.vote.godmodeskip

import java.util.*

class GodModeSkipVote {

    val agreePlayers = ArrayList<UUID>()
    val disagreePlayers = ArrayList<UUID>()


    /**
     * 플레이어를 무적 해제 투표에 동의시킵니다.
     * 반대 중이었다면 반대를 취소합니다.
     */
    fun voteToAgree(playerUuid: UUID) {
        disagreePlayers.remove(playerUuid)
        agreePlayers.add(playerUuid)
    }

    /**
     * 플레이어를 무적 해제 투표에 반대시킵니다.
     * 동의 중이었다면 동의를 취소합니다.
     */
    fun  voteToDisagree(playerUuid: UUID) {
        agreePlayers.remove(playerUuid)
        disagreePlayers.add(playerUuid)
    }

    /**
     * 플레이어의 투표를 취소시킵니다.
     */
    fun unvote(playerUuid: UUID) {
        agreePlayers.remove(playerUuid)
        disagreePlayers.remove(playerUuid)
    }

    /**
     * 플레이어의 투표 찬성 여부를 반환합니다.
     */
    fun isAgree(playerUuid: UUID): Boolean {
        return agreePlayers.contains(playerUuid)
    }

    /**
     * 플레이어의 투표 반대 여부를 반환합니다.
     */
    fun isDisagree(playerUuid: UUID): Boolean {
        return disagreePlayers.contains(playerUuid)
    }

    /**
     * 플레이어의 투표 여부를 반환합니다.
     */
    fun isVoted(playerUuid: UUID): Boolean {
        return isAgree(playerUuid) || isDisagree(playerUuid)
    }

    /**
     * 모든 투표를 취소합니다.
     */
    fun clear() {
        agreePlayers.clear()
        disagreePlayers.clear()
    }

}