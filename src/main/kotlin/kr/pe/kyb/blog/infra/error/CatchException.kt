package kr.pe.kyb.blog.infra.error

//https://www.podo-dev.com/blogs/278

fun <T> catchDomainException(f: () -> T): T = try {
    f()
} catch (e: DomainException) {
    throw ServiceException.asServiceException(e)
} catch (e: ServiceException) {
    throw e
}

fun <T> catchServiceException(isDebug: Boolean = false, f: () -> T): T = try {
    f()
} catch (e: ServiceException) {
    throw ControllerException.asControllerException(e, isDebug)
} catch (e: ControllerException) {
    throw e
}

