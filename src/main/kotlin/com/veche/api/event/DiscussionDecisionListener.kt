package com.veche.api.event

import com.veche.api.database.model.ActionType
import com.veche.api.database.model.PendingActionEntity
import com.veche.api.database.repository.PendingActionRepository
import com.veche.api.dto.company.CompanyUpdateDto
import com.veche.api.dto.party.PartyUpdateDto
import com.veche.api.service.CompanyService
import com.veche.api.service.PartyService
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

// TODO : @Retryable
@Component
class DiscussionDecisionListener(
    private val actionRepository: PendingActionRepository,
    private val payloadMapper: PayloadMapper,
    private val partyService: PartyService,
    private val companyService: CompanyService,
) {
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun onDiscussionResolved(event: DiscussionResolvedEvent) {
        if (!event.approved) return

        actionRepository
            .findAllByDiscussionIdAndExecutedFalse(event.discussionId)
            .forEach { executeAndMark(it) }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun executeAndMark(entity: PendingActionEntity) {
        when (entity.actionType) {
            ActionType.RENAME_PARTY -> {
                val p =
                    payloadMapper.fromJson(
                        entity.payload,
                        ChangePartyName::class.java,
                    )
                partyService.updateParty(PartyUpdateDto(p.newName), p.partyId)
            }

            ActionType.RENAME_COMPANY -> {
                val p =
                    payloadMapper.fromJson(
                        entity.payload,
                        ChangeCompanyName::class.java,
                    )
                companyService.updateCompanyName(p.companyId, CompanyUpdateDto(p.newName))
            }

            ActionType.EVICT_USER_FROM_PARTY -> {
                val p =
                    payloadMapper.fromJson(
                        entity.payload,
                        EvictUserFromParty::class.java,
                    )
                partyService.evictUserFromParty(p.partyId, p.userId)
            }

            ActionType.ADD_USER_TO_PARTY -> {
                val p =
                    payloadMapper.fromJson(
                        entity.payload,
                        AddUserToParty::class.java,
                    )
                partyService.addUserToParty(p.partyId, p.userId)
            }
        }

        entity.executed = true
    }
}
