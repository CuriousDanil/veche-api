package com.veche.api.database.repository

import com.veche.api.database.model.CompanyEntity
import com.veche.api.database.model.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface UserRepository : JpaRepository<UserEntity, UUID> {
    @Query(
        """
  select u
  from UserEntity u
  where u.company = :company
    and (
      lower(u.name)  like lower(concat('%', :q, '%'))
      or lower(u.email) like lower(concat('%', :q, '%'))
    )
  """,
    )
    fun search(
        @Param("company") company: CompanyEntity,
        @Param("q") q: String,
    ): List<UserEntity>

    fun findByEmail(email: String): UserEntity?

    fun existsByEmail(email: String): Boolean
}
