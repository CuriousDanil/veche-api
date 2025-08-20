package com.veche.api.event

import com.veche.api.database.model.ActionType
import com.veche.api.database.model.PendingActionEntity
import com.veche.api.database.repository.PendingActionRepository
import com.veche.api.dto.company.CompanyUpdateDto
import com.veche.api.dto.party.PartyUpdateDto
import com.veche.api.service.CompanyService
import com.veche.api.service.PartyService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class DiscussionDecisionListener(
    private val actionRepository: PendingActionRepository,
    private val payloadMapper: PayloadMapper,
    private val partyService: PartyService,
    private val companyService: CompanyService,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun onDiscussionResolved(event: DiscussionResolvedEvent) {
        if (!event.approved) {
            log.info("Discussion ${event.discussionId} was not approved. No actions will be executed.")
            return
        }

        log.info("Discussion ${event.discussionId} was approved. Executing pending actions.")
        actionRepository
            .findAllByDiscussionIdAndExecutedFalse(event.discussionId)
            .forEach { action ->
                try {
                    executeAndMark(action)
                    log.info("Successfully executed action ${action.id} of type ${action.actionType}")
                } catch (e: Exception) {
                    log.error("Failed to execute action ${action.id} of type ${action.actionType}", e)
                }
            }
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

            ActionType.DELETE_PARTY -> {
                val p =
                    payloadMapper.fromJson(
                        entity.payload,
                        DeleteParty::class.java,
                    )
                partyService.deleteParty(p.partyId)
            }
        }

        entity.executed = true
        actionRepository.save(entity)
    }
}
