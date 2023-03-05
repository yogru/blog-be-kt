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
    roleEnum: RoleEum
) : JPABaseEntity() {
    @Id
    @Column(length = 255, nullable = false)
    @Enumerated(EnumType.STRING)
    val id: RoleEum = roleEnum
}

@Entity
class UserRole(
    user: UserEntity,
    role: Role
) : JPABaseEntity() {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    val id: UUID? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: UserEntity = user

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", foreignKey = ForeignKey(name = "fk_user_role_role"))
    var role: Role = role
}

@Entity
class UserEntity(
    id: UUID? = null,
    account: String,
    password: String,
    status: UserStatus = UserStatus.SIGN,
    nickName: String,
    roles: List<Role>
) : JPABaseEntity() {

    @Id
    @Column(columnDefinition = "BINARY(16)")
    val id: UUID = id ?: UUID.randomUUID()

    @Column(length = 255, unique = true, nullable = false)
    var account: String = account

    @Column(length = 255)
    var password: String = password

    @Column(length = 255, nullable = false)
    @Enumerated(EnumType.STRING)
    var status: UserStatus = status

    @Column(length = 255)
    var nickName: String = nickName

    @OneToMany(mappedBy = "user", cascade = [CascadeType.PERSIST], orphanRemoval = true)
    var userRoles: Set<UserRole> = roles.map { UserRole(user = this, role = it) }.toSet()

    val roleStrings: List<String>
        get() = this.userRoles.map { it.role.id.toString() }
}
