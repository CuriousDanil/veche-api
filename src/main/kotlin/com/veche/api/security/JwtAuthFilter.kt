package com.veche.api.security

import com.veche.api.database.repository.UserRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.slf4j.LoggerFactory

@Component
class JwtAuthFilter(
    private val jwtService: JwtService,
    private val userRepository: UserRepository
) : OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger(JwtAuthFilter::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")
        log.debug(">>> JwtAuthFilter for {}  Authorization={}", request.requestURI, authHeader)

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("No bearer header, skipping")
            filterChain.doFilter(request, response)
            return
        }

        val jwt = authHeader.substring(7)
        if (!jwtService.validateAccessToken(jwt)) {
            log.debug("Token failed validation")
            filterChain.doFilter(request, response)
            return
        }

        val userId = jwtService.extractUserId(jwt)
        val user = userRepository.findById(userId).orElseThrow {
            UsernameNotFoundException("User no longer exists")
        }
        val principal = UserPrincipal.fromEntity(user)

        val authToken = UsernamePasswordAuthenticationToken(principal, null, listOf())
        SecurityContextHolder.getContext().authentication = authToken

        log.debug("Authentication set for user {}", userId)

        filterChain.doFilter(request, response)
    }
}