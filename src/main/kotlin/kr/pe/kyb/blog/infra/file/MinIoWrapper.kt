package kr.pe.kyb.blog.infra.file

import io.minio.BucketExistsArgs
import io.minio.MakeBucketArgs
import io.minio.MinioClient
import kr.pe.kyb.blog.infra.error.InfraException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component


@Component
data class MinIOConfigValues(
    @Value("\${file.minIO.endpoint}")
    val endpoint: String,

    @Value("\${file.minIO.port}")
    val port: Int,

    @Value("\${file.minIO.accessKey}")
    val accessKey: String,

    @Value("\${file.minIO.secretAccessKey}")
    val secretAccessKey: String,

    @Value("\${file.minIO.defaultBucketName}")
    val defaultBucketName: String
)

@Component
class MinIoWrapper(
    private val configValues: MinIOConfigValues
) : ObjectStorage {

    private fun getClient(): MinioClient {
        return MinioClient.builder()
            .endpoint(configValues.endpoint, configValues.port, false)
            .credentials(configValues.accessKey, configValues.secretAccessKey)
            .build()
    }

    override fun makeBucket(name: String): Boolean {
        val client = this.getClient()
        val found = client.bucketExists(
            BucketExistsArgs
                .builder()
                .bucket(name)
                .build()
        )
        if (found) {
            throw InfraException("이미 존재하는 버킷 다시 생성 요청")
        }
        client.makeBucket(
            MakeBucketArgs.builder()
                .bucket(name)
                .build()
        )
        return true
    }

}