package com.veche.api.controller

import com.veche.api.dto.company.CompanyResponseDto
import com.veche.api.security.UserPrincipal
import com.veche.api.service.CompanyService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/company")
class CompanyController(
    private val companyService: CompanyService,
) {
    @GetMapping("/my-company")
    fun getMyCompany(
        @AuthenticationPrincipal user: UserPrincipal,
    ): CompanyResponseDto = companyService.getCompanyForUser(user)
}
