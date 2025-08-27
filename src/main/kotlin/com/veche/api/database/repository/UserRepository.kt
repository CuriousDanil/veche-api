package com.veche.api.database.repository

import com.veche.api.database.model.CompanyEntity
import com.veche.api.database.model.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

/**
 * TODO()
 */
interface UserRepository : JpaRepository<UserEntity, UUID> {
    /**
     * TODO()
     *
     * @param company TODO()
     * @param q TODO()
     * @return TODO()
     */
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

    /**
     * TODO()
     *
     * @param email TODO()
     * @return TODO()
     */
    fun findByEmail(email: String): UserEntity?

    /**
     * TODO()
     *
     * @param email TODO()
     * @return TODO()
     */
    fun existsByEmail(email: String): Boolean
}
