package kr.pe.kyb.blog;


import kr.pe.kyb.blog.infra.anotation.RestV2
import kr.pe.kyb.blog.infra.config.ConstantValue
import org.springframework.security.access.annotation.Secured
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController;

@RestV2
class HealthController {
    
    @GetMapping("/health-check")
    fun healthCheck(): String {
        return "OK"
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/admin/health-check")
    fun adminHealthCheck(): String {
        return "OK"
    }
}
