package com.veche.api.service

import com.anthropic.client.AnthropicClient
import com.anthropic.models.messages.MessageCreateParams
import com.anthropic.models.messages.Model
import com.veche.api.database.model.DiscussionEntity
import com.veche.api.database.model.SummaryEntity
import com.veche.api.database.repository.SummaryRepository
import com.veche.api.dto.summary.SummaryResponseDto
import com.veche.api.mapper.SummaryMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SummaryService(
    private val client: AnthropicClient,
    private val summaryRepository: SummaryRepository,
    private val summaryMapper: SummaryMapper,
) {
    @Transactional
    fun summarizeComments(
        discussion: DiscussionEntity,
        comments: List<String>,
    ): SummaryResponseDto {
        val params =
            MessageCreateParams
                .builder()
                .model(Model.CLAUDE_OPUS_4_0)
                .temperature(0.2)
                .maxTokens(200_000)
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

        val message = client.messages().create(params)
        // Extract text parts from the response content

        val summary =
            SummaryEntity().apply {
                this.discussion = discussion
                content = message.content().joinToString("")
            }

        return summaryMapper.toDto(summaryRepository.save(summary))
    }
}
