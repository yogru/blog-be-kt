package kr.pe.kyb.blog.domain.user

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

enum class RoleEum {
    ADMIN,
    USER
}

// https://gksdudrb922.tistory.com/217
@Entity
class Role(
    @Id
    @Column(length = 255, nullable = false)
    @Enumerated(EnumType.STRING)
    val id: RoleEum
) : JPABaseEntity()

@Entity
class UserRole(
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    val id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: UserEntity,


    @Column(name = "role_id", length = 255, nullable = false)
    @Enumerated(EnumType.STRING)
    val roleId: RoleEum,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "role_id",
        updatable = false,
        insertable = false,
        foreignKey = ForeignKey(name = "fk_user_role_role")
    )
    var role: Role? = null
) : JPABaseEntity()

@Entity
class UserEntity(
    id: UUID? = null,

    @Column(length = 255, unique = true, nullable = false)
    var account: String,

    @Column(length = 255)
    var password: String,

    @Column(length = 255, nullable = false)
    @Enumerated(EnumType.STRING)
    var status: UserStatus,

    @Column(length = 255)
    var nickName: String,
) : JPABaseEntity() {

    @Id
    @Column(columnDefinition = "BINARY(16)")
    val id: UUID

    @OneToMany(mappedBy = "user", cascade = [CascadeType.PERSIST], orphanRemoval = true)
    var userRoles: Set<UserRole> = HashSet()

    init {
        this.id = id ?: UUID.randomUUID()
        this.userRoles = setOf(
            UserRole(user = this, roleId = RoleEum.USER)
        )
    }

    val roles: List<String>
        get() = this.userRoles.map { it.roleId.toString() }
}
