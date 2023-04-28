package kr.pe.kyb.blog.infra.file

interface ObjectStorage {
    fun makeBucket(name: String): Boolean
}