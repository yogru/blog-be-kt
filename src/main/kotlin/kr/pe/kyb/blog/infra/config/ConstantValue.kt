package kr.pe.kyb.blog.infra.config

object ConstantValue {
    const val API_PREFIX: String = "/api/v2"
    const val IS_DEBUG = true
    const val INTERNAL_HTTP_BASE: String = "http://localhost:8080"

    val INTERNAL_HTTP: String
        get() = ConstantValue.INTERNAL_HTTP_BASE + ConstantValue.API_PREFIX

}