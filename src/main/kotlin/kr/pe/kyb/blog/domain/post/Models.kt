package kr.pe.kyb.blog.domain.post

import jakarta.persistence.*
import kr.pe.kyb.blog.infra.persistence.JPABaseEntity
import org.hibernate.annotations.GenericGenerator
import java.util.*
import kotlin.collections.HashSet


@Entity
class Tag(
    @Id
    @Column(length = 255, nullable = false)
    val id: String
) : JPABaseEntity()


@Entity
class PostTag(
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    val id: UUID? = null,

    @Column(name = "tag_id", length = 255, nullable = false)
    var tagId: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    var post: Post,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "tag_id",
        updatable = false,
        insertable = false,
        foreignKey = ForeignKey(name = "fk_post_tag_tag")
    )
    var tag: Tag? = null,

    ) : JPABaseEntity()


@Entity
class PostUserValue(
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    val id: UUID,

    @Column(length = 255, nullable = false)
    val account: String,

    @Column(length = 255, nullable = false)
    val nickName: String,
) : JPABaseEntity()

@Entity
class Post(
    @Column(length = 255, nullable = false)
    var title: String,

    @Column(length = 255, nullable = false)
    var body: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val writer: PostUserValue,

    tags: List<String>
) : JPABaseEntity() {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    var id: UUID? = null

    @Column()
    var deleted: Boolean = false

    @OneToMany(mappedBy = "post", cascade = [CascadeType.PERSIST], orphanRemoval = true)
    var postTags: Set<PostTag> = HashSet()


    init {
        id = null
        deleted = false
        postTags = tags.map { PostTag(tagId = it, post = this) }.toSet()
    }

    val tags: Set<String>
        get() = postTags.map { it.tagId }.toSet()

    val writerId: UUID
        get() = writer.id

    val writerName: String
        get() = writer.nickName

    val writerEmail: String
        get() = writer.account
}


@Entity
class SeriesPost(
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    val id: UUID? = null,

    @Column()
    var orderNumber: Int,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "series_id", nullable = false)
    val series: Series,

    @Column(name = "post_id", columnDefinition = "BINARY(16)", nullable = false)
    val postId: UUID,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "post_id",
        updatable = false,
        insertable = false,
        foreignKey = ForeignKey(name = "fk_series_post_post_id")
    )
    var post: Post? = null

) : JPABaseEntity() {}


@Entity
class Series(
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    val id: UUID? = null,

    @Column(columnDefinition = "BINARY(16)", nullable = false)
    var userId: UUID,

    @Column(length = 255, nullable = false)
    var title: String,

    @Column(length = 255, nullable = false)
    var body: String,

    @OneToMany(mappedBy = "series", cascade = [CascadeType.PERSIST], orphanRemoval = true)
    var seriesPosts: Set<SeriesPost> = HashSet()
) : JPABaseEntity() {}