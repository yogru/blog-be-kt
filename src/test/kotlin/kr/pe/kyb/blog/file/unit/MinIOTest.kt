package kr.pe.kyb.blog.file.unit

import kr.pe.kyb.blog.infra.file.MinIoWrapper
import kr.pe.kyb.blog.mock.MyTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@MyTest
@DisplayName("unit: files > MinIoTest")
class MinIOTest {

    @Autowired
    lateinit var minIO: MinIoWrapper

    @Test
    fun makeBucket() {
        minIO.makeBucket("test")
    }
}