package com.veche.api.service

import com.anthropic.client.AnthropicClient
import com.anthropic.models.messages.MessageCreateParams
import com.anthropic.models.messages.Model
import com.veche.api.database.model.DiscussionEntity
import com.veche.api.database.model.SummaryEntity
import com.veche.api.database.repository.SummaryRepository
import com.veche.api.dto.summary.SummaryResponseDto
import com.veche.api.mapper.SummaryMapper
import com.veche.api.security.UserPrincipal
import org.slf4j.LoggerFactory // <-- add this import
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class SummaryService(
    private val client: AnthropicClient,
    private val summaryRepository: SummaryRepository,
    private val summaryMapper: SummaryMapper,
) {
    private val logger = LoggerFactory.getLogger(SummaryService::class.java) // <-- declare a logger

    @Transactional
    fun getSummaryForDiscussion(
        user: UserPrincipal,
        discussionId: UUID,
    ): SummaryResponseDto = summaryMapper.toDto(summaryRepository.findAllByDiscussionId(discussionId).minByOrNull { it.createdAt }!!)

    @Transactional
    fun summarizeComments(
        discussion: DiscussionEntity,
        comments: List<String>,
    ): SummaryResponseDto {
        // Log the start of summarization and incoming comments
        logger.info("Generating summary for {} comments in discussion {}", comments.size, discussion)
        logger.debug("Comments: {}", comments)

        val params =
            MessageCreateParams
                .builder()
                .model(Model.CLAUDE_3_5_SONNET_LATEST)
                .temperature(0.2)
                .maxTokens(20000)
                .system(
                    """
You are a meticulous discussion synthesizer. Your job is to turn a raw set of comments into a single, readable document that preserves EVERY unique point and take, while de-duplicating overlaps and organizing them clearly.

Non-negotiables:
- Do NOT drop any concrete detail, example, caveat, number, link, or edge case.
- Merge duplicates, but keep each distinct nuance—cite where each point came from.
- Mirror the comment language: if comments are in Russian, write in Russian; if mixed, keep each quoted fragment in its original language and write surrounding prose in the majority language.
- No hallucinations; if something is missing or ambiguous, state that explicitly.
- Prefer clear, skimmable structure in Markdown. Long output is fine.

Output format (Markdown):
1) **Executive overview**
2) **Theme map** (with refs like [#3,#7])
3) **Detailed synthesis by theme** (bullets with refs)
4) **Minority/edge cases**
5) **Chronology** (if timestamps present)
6) **Open questions / unknowns**
7) **Actionable next steps** (if applicable)
8) **Per-comment coverage ledger** (points captured for every #i)

Conventions:
- Use refs like [#12,#14] consistently.
- Preserve URLs and numbers exactly.
- If hitting length limits, insert [[CONTINUE]] and resume seamlessly when prompted.
                    """.trimIndent(),
                ).addUserMessage(
                    """
Summarize and synthesize the following comments into a single document per the rules above.
Do not omit any detail. Keep the comments’ language. Organize by themes and include the per-comment coverage ledger.

${comments.joinToString("\n")}
                    """.trimIndent(),
                ).build()

        // Log before sending the request
        logger.info("Sending summarization request to Anthropic")
        val message = client.messages().create(params)
        // Log after receiving the response
        logger.info("Received summarization response with {} parts", message.content().size)

        val summary =
            SummaryEntity().apply {
                this.discussion = discussion
                content = message.content().joinToString("")
            }

        // Log before saving the summary
        logger.info("Saving summary for discussion {}", discussion)
        return summaryMapper.toDto(summaryRepository.save(summary))
    }
}
