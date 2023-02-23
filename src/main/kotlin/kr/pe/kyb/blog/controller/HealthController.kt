package kr.pe.kyb.blog.controller;


import kr.pe.kyb.blog.infra.anotation.RestV2
import kr.pe.kyb.blog.infra.config.ConstantValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController;

@RestV2
class HealthController {
    @GetMapping("/health-check")
    fun healthCheck(): String {
        return "OK"
    }
}
