package kr.pe.kyb.blog.domain.post

import jakarta.persistence.*
import kr.pe.kyb.blog.infra.persistence.JPABaseEntity
import org.hibernate.annotations.GenericGenerator
import java.util.*


@Entity
class Tag(
    tagName: String
) : JPABaseEntity() {
    @Id
    @Column(length = 255, nullable = false)
    val id: String = tagName
}


@Entity
class PostTag(
    post: Post,
    tag: Tag
) : JPABaseEntity() {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    val id: UUID? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    var post: Post = post

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "tag_id",
        updatable = false,
        insertable = false,
        foreignKey = ForeignKey(name = "fk_post_tag_tag")
    )
    var tag: Tag = tag
}


@Entity
class PostUserValue(
    @Id
    @Column(columnDefinition = "BINARY(16)")
    val id: UUID,

    @Column(length = 255, nullable = false)
    val account: String,

    @Column(length = 255, nullable = false)
    val nickName: String,
) : JPABaseEntity()

@Entity
class Post(
    title: String,
    body: String,
    writer: PostUserValue,
    tags: List<Tag>
) : JPABaseEntity() {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    var id: UUID? = null

    @Column(length = 255, nullable = false)
    var title: String = title

    @Column(length = 255, nullable = false)
    var body: String = body

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val writer: PostUserValue = writer

    @Column
    var deleted: Boolean = false

    @OneToMany(mappedBy = "post", cascade = [CascadeType.PERSIST], orphanRemoval = true)
    var postTags: MutableSet<PostTag> = tags.map { PostTag(tag = it, post = this) }.toMutableSet()


    val tagNames: Set<String>
        get() = postTags.map { it.tag.id }.toSet()

    val writerId: UUID
        get() = writer.id

    val writerName: String
        get() = writer.nickName

    val writerEmail: String
        get() = writer.account


    fun update(title: String?, body: String?, tags: List<Tag>?) {
        if (!body.isNullOrEmpty()) {
            this.body = body
        }
        if (!title.isNullOrEmpty()) {
            this.title = title
        }
        if (!tags.isNullOrEmpty()) {
            this.postTags.clear()
            tags.map { PostTag(tag = it, post = this) }
                .forEach { this.postTags.add(it) }
        }
    }
}


@Entity
class SeriesPost(
    orderNumber: Int,
    series: Series,
    post: Post
) : JPABaseEntity() {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    val id: UUID? = null

    @Column()
    var orderNumber: Int = orderNumber

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "series_id", nullable = false)
    val series: Series = series

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "post_id",
        foreignKey = ForeignKey(name = "fk_series_post_post_id")
    )
    var post: Post = post
}


@Entity
class Series(
    writer: PostUserValue,
    title: String,
    body: String,
    posts: List<Post>,
) : JPABaseEntity() {


    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    val id: UUID? = null

    @Column(length = 255, nullable = false)
    var title: String = title

    @Column(length = 255, nullable = false)
    var body: String = body

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "user_id",
        nullable = false,
        foreignKey = ForeignKey(name = "fk_series_user")
    )
    var writer: PostUserValue = writer

    @OneToMany(mappedBy = "series", cascade = [CascadeType.ALL], orphanRemoval = true)
    var seriesPosts: MutableSet<SeriesPost> = posts.mapIndexed { index, post ->
        SeriesPost(post = post, orderNumber = index + 1, series = this)
    }.toMutableSet()


    fun update(title: String?, body: String?, posts: List<Post>?) {
        if (title != null) {
            this.title = title
        }
        if (body != null) {
            this.body = body
        }
        if (posts != null) {
            this.seriesPosts.clear()
            val newSeriesPosts = posts.mapIndexed { index, post ->
                SeriesPost(post = post, orderNumber = index + 1, series = this)
            }
            this.seriesPosts.addAll(newSeriesPosts)
        }
    }

}