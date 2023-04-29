package kr.pe.kyb.blog.infra

import kr.pe.kyb.blog.infra.error.InfraException
import kr.pe.kyb.blog.infra.file.MinIoWrapper
import kr.pe.kyb.blog.mock.MyTest
import org.apache.http.entity.ContentType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ResourceLoader
import java.util.UUID

@MyTest
@DisplayName("unit: files > MinIoTest")
class MinIOTest {

    @Autowired
    lateinit var minIO: MinIoWrapper

    @Autowired
    lateinit var resourceLoader: ResourceLoader

    @Test
    fun makeBucket() {
        Assertions.assertThrows(InfraException::class.java) {
            minIO.makeBucket("test")
        }

    }


    @Test
    fun putAndGetObject() {
        val fileName = "dog.jpg"
        val uuidId = UUID.randomUUID().toString()
        var resource = resourceLoader.getResource("classpath:$fileName")
        var inputStream = resource.inputStream
        minIO.putObject(uuidId, inputStream, ContentType.IMAGE_JPEG)
        val bytes = minIO.getObject(uuidId)

        resource = resourceLoader.getResource("classpath:$fileName")
        inputStream = resource.inputStream
        Assertions.assertEquals(inputStream.readAllBytes().size, bytes.size)
        minIO.removeObject(uuidId)
    }

}