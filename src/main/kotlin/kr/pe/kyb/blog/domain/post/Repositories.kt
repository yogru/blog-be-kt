package kr.pe.kyb.blog.domain.post

import kr.pe.kyb.blog.infra.persistence.JpaBaseRepository
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

@Repository
class PostAggregateRepository(
    private val postRepository: PostRepository,
    private val tagRepository: TagRepository,
    private val postUserValueRepository: PostUserValueRepository,
    private val seriesRepository: SeriesRepository,
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
}