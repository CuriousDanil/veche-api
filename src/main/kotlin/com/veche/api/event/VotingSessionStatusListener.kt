package com.veche.api.event

import com.veche.api.database.model.DiscussionStatus
import com.veche.api.database.model.VotingSessionEntity
import com.veche.api.database.model.VotingSessionStatus
import com.veche.api.database.repository.VotingSessionRepository
import com.veche.api.service.VotingSessionService
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.postgresql.PGConnection
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate
import java.sql.SQLException
import java.util.UUID
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.sql.DataSource

@Component
class VotingSessionStatusListener(
    private val dataSource: DataSource,
    private val votingSessionService: VotingSessionService,
) {
    private val log = LoggerFactory.getLogger(javaClass)
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()

    @Volatile
    private var running = true

    companion object {
        private const val START_CHANNEL = "voting_session_start"
        private const val SECOND_PHASE_CHANNEL = "voting_session_second_phase"
        private const val END_CHANNEL = "voting_session_end"
        private const val RETRY_DELAY_MS = 10000L
    }

    @PostConstruct
    fun start() {
        executor.submit { listenLoop() }
    }

    private fun listenLoop() {
        while (running) {
            try {
                dataSource.connection.use { connection ->
                    log.info("Establishing listener connection to PostgreSQL.")
                    val pgConnection = connection.unwrap(PGConnection::class.java)

                    connection.createStatement().use { stmt ->
                        stmt.execute("LISTEN $START_CHANNEL")
                        stmt.execute("LISTEN $SECOND_PHASE_CHANNEL")
                        stmt.execute("LISTEN $END_CHANNEL")
                    }

                    while (running && !connection.isClosed) {
                        val notifications = pgConnection.getNotifications(10000)
                        notifications?.forEach { notification ->
                            log.info(
                                "Received notification on channel '{}' with payload: {}",
                                notification.name,
                                notification.parameter,
                            )
                            handleNotification(notification.name, notification.parameter)
                        }
                    }
                }
            } catch (e: SQLException) {
                log.error("Database connection failed for listener. Retrying in ${RETRY_DELAY_MS}ms.", e)
                sleepQuietly(RETRY_DELAY_MS)
            } catch (e: Exception) {
                log.error("An unexpected error occurred in the listener loop. Retrying in ${RETRY_DELAY_MS}ms.", e)
                sleepQuietly(RETRY_DELAY_MS)
            }
        }
        log.info("Listener loop has terminated.")
    }

    private fun handleNotification(
        channel: String,
        payload: String,
    ) {
        try {
            val sessionId = UUID.fromString(payload)
            when (channel) {
                START_CHANNEL -> votingSessionService.startVotingSession(sessionId)
                SECOND_PHASE_CHANNEL -> votingSessionService.startVotingSession(sessionId)
                END_CHANNEL -> votingSessionService.endVotingSession(sessionId)
                else -> {
                    log.warn("Unknown notification channel: $channel")
                }
            }
        } catch (e: Exception) {
            log.error("Failed to process notification for session ID: $payload on channel $channel", e)
        }
    }

    private fun sleepQuietly(ms: Long) {
        try {
            Thread.sleep(ms)
        } catch (ie: InterruptedException) {
            Thread.currentThread().interrupt()
        }
    }

    @PreDestroy
    fun stop() {
        log.info("Stopping PostgreSQL notification listener.")
        running = false
        executor.shutdown()
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow()
            }
        } catch (e: InterruptedException) {
            executor.shutdownNow()
        }
    }
}
