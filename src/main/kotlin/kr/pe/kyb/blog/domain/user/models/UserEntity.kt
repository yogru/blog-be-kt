package kr.pe.kyb.blog.domain.user.models

import jakarta.persistence.*
import kr.pe.kyb.blog.infra.persistence.JPABaseEntity
import org.hibernate.annotations.GenericGenerator
import java.util.UUID

enum class UserStatus(
    status: String
) {
    SIGN("sign"),
    NORMAL("normal"),
    WITHDRAW("withdraw"),
    REMOVE("remove")
}


@Entity
class UserEntity(
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    val id: UUID? = null,

    @Column(length = 255, unique = true, nullable = false)
    var account: String,

    @Column(length = 255)
    var password: String,

    @Column(length = 255, nullable = false)
    @Enumerated(EnumType.STRING)
    var status: UserStatus,

    @Column(length = 255)
    var nickName: String

) : JPABaseEntity() {
}