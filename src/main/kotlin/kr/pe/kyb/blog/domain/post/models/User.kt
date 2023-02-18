package kr.pe.kyb.blog.domain.post.models

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import kr.pe.kyb.blog.infra.persistence.JPABaseEntity
import org.hibernate.annotations.GenericGenerator
import java.util.UUID

@Entity
class User(
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    val id: UUID?,

    @Column(length = 255, unique = true, nullable = false)
    var account: String,

    @Column(length = 255)
    var password: String,

    @Column(length = 255, nullable = false)
    var status: String,

    @Column(length = 255)
    var nickName: String

) : JPABaseEntity() {
}