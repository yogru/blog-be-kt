package kr.pe.kyb.blog.infra.file

import org.apache.http.entity.ContentType
import java.io.InputStream

interface ObjectStorage {
    fun makeBucket(name: String): Boolean

    fun putObject(
        objectName: String,
        inputStream: InputStream,
        contentType: ContentType
    )

    fun getObject(
        objectName: String
    ): ByteArray

    fun removeObject(objectName: String, versionId: String? = null)
}