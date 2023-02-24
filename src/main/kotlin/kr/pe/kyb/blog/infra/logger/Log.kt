package kr.pe.kyb.blog.infra.logger

import org.slf4j.LoggerFactory

interface Log {
    fun logger() = LoggerFactory.getLogger(this.javaClass)
}

