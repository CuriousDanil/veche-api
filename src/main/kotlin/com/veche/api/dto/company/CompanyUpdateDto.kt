package com.veche.api.dto.company

/**
 * Data Transfer Object for updating an existing company.
 *
 * @property name The new name for the company
 */
data class CompanyUpdateDto(
    val name: String
)
