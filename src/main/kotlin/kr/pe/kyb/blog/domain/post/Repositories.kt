package kr.pe.kyb.blog.domain.post

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import kr.pe.kyb.blog.domain.post.QPost.post
import kr.pe.kyb.blog.domain.post.QPostTag.postTag
import kr.pe.kyb.blog.domain.post.QPostUserValue.postUserValue
import kr.pe.kyb.blog.domain.post.QSeries.series
import kr.pe.kyb.blog.domain.post.QTag.tag
import kr.pe.kyb.blog.infra.persistence.JpaBaseRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*

interface PostRepository : JpaRepository<Post, UUID> {
    @Query(value = "SELECT p FROM Post p join fetch p.writer where p.id =:id")
    fun findByIdFetchUserValue(id: UUID): Post?


    @Query(
        value = "SELECT p FROM Post p  " +
                "left join fetch p.postTags postTags " +
                "left join fetch postTags.tag " +
                "where p.id in :ids"
    )
    fun findInIds(ids: List<UUID>): List<Post>
}

interface SeriesRepository : JpaRepository<Series, UUID> {
    @Query(
        value = " SELECT s FROM Series s " +
                " join fetch s.writer " +
                " left join fetch s.seriesPosts seriesPost left join fetch seriesPost.post " +
                " where s.id =:id "
    )
    fun fetchSeries(id: UUID): Series?

}

interface PostUserValueRepository : JpaRepository<PostUserValue, UUID> {}
interface TagRepository : JpaRepository<Tag, String> {
    @Modifying
    @Query(
        value = "INSERT INTO tag(id, created_at, updated_at) VALUES(:tagName, :createdAt, :updatedAt) " +
                "ON DUPLICATE KEY UPDATE id=:tagName, updated_at=:updatedAt", nativeQuery = true
    )
    fun upsertTag(
        tagName: String,
        createdAt: LocalDateTime = LocalDateTime.now(),
        updatedAt: LocalDateTime = LocalDateTime.now()
    )

    @Query(value = "SELECT t FROM Tag t where t.id in :ids")
    fun findInIds(ids: List<String>): List<Tag>

}


data class PostCondition(
    val tagNames: List<String>,
    val deleted: Boolean = false,
    val title: String? = null
)

@Repository
class PostAggregateRepository(
    private val postRepository: PostRepository,
    private val tagRepository: TagRepository,
    private val postUserValueRepository: PostUserValueRepository,
    private val seriesRepository: SeriesRepository,
    private val query: JPAQueryFactory
) : JpaBaseRepository() {

    fun findTagById(tagName: String): Tag? {
        return tagRepository.findById(tagName).let {
            if (it.isEmpty) null else it.get()
        }
    }

    fun findPostById(id: UUID): Post? {
        return postRepository.findById(id).let {
            if (it.isEmpty) null else it.get()
        }
    }

    fun findByIdFetchUserValue(id: UUID): Post? {
        return postRepository.findByIdFetchUserValue(id)
    }

    fun upsertTag(tagName: String) {
        tagRepository.upsertTag(tagName)
    }

    fun findAllTag(): List<Tag> {
        return tagRepository.findAll()
    }

    fun findUserValueById(id: UUID): PostUserValue? {
        return postUserValueRepository.findById(id)
            .let { if (it.isEmpty) null else it.get() }
    }

    fun findSeriesById(id: UUID): Series? {
        return seriesRepository.findById(id)
            .let { if (it.isEmpty) null else it.get() }
    }

    fun fetchSeries(id: UUID): Series? {
        return seriesRepository.fetchSeries(id)
    }

    fun findPostInIds(ids: List<UUID>?): List<Post>? {
        if (ids == null) return null
        if (ids.isEmpty()) return listOf()
        return postRepository.findInIds(ids)
    }

    fun findTagInIds(ids: List<String>?): List<Tag> {
        if (ids.isNullOrEmpty()) return listOf()
        return tagRepository.findInIds(ids!!)
    }

    fun listSeries(pageable: Pageable): List<Series> {
        return query.selectFrom(series)
            .leftJoin(series.writer, postUserValue)
            .orderBy(series.createdAt.desc())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()
    }

    fun listPost(condition: PostCondition, pageable: Pageable): List<Post> {
        fun eqDelete(): BooleanExpression {
            return post.deleted.eq(condition.deleted)
        }

        fun eqTitle(): BooleanExpression? {
            val title = condition.title ?: return null
            return post.title.like("%$title%")
        }

        fun inTagNames(): BooleanExpression? {
            if (condition.tagNames.isNullOrEmpty()) return null
            println(condition.tagNames)
            return tag.id.`in`(condition.tagNames)
        }
        return query.selectDistinct(post)
            .from(post)
            .leftJoin(post.writer, postUserValue)
            .leftJoin(post.postTags, postTag)
            .leftJoin(postTag.tag, tag)
            .where(
                eqDelete(),
                eqTitle(),
                inTagNames()
            )
            .orderBy(post.createdAt.desc())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()
    }


}