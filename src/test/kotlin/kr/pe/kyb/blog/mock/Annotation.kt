package kr.pe.kyb.blog.mock

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import org.springframework.transaction.annotation.Transactional


@SpringBootTest
@AutoConfigureMockMvc
@SpringJUnitConfig(TestConfig::class)
@Transactional(readOnly = true)
annotation class MyTest()
