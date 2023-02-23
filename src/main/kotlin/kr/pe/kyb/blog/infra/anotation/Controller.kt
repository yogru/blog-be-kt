package kr.pe.kyb.blog.infra.anotation

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
annotation class RestV1


@RestController
@RequestMapping("/api/v2")
annotation class RestV2

