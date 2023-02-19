package kr.pe.kyb.blog

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication(exclude = [SecurityAutoConfiguration::class])
class BlogApplication

fun main(args: Array<String>) {
    runApplication<BlogApplication>(*args)
}
