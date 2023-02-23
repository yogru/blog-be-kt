package kr.pe.kyb.blog.infra.error

open class LayerException(
    val code: ErrorCode,
    val stackElement: StackTraceElement
) : RuntimeException() {}


class DomainException private constructor(
    code: ErrorCode,
    stackElement: StackTraceElement
) : LayerException(code, stackElement) {
    companion object {
        fun createException(code: ErrorCode): DomainException {
            val stackTraceElement = Throwable().stackTrace[1]
            return DomainException(code, stackTraceElement)
        }
    }
}


class ServiceException private constructor(
    code: ErrorCode,
    stackElement: StackTraceElement
) : LayerException(code, stackElement) {
    companion object {
        fun createException(code: ErrorCode): ServiceException {
            val stackTraceElement = Throwable().stackTrace[1]
            return ServiceException(code, stackTraceElement)
        }

        fun asServiceException(
            d: DomainException,
            replaceMessage: String? = null,
            replaceStackElement: Boolean = false
        ): ServiceException {
            d.code.message = replaceMessage ?: d.code.message
            val stackElement = if (replaceStackElement) Throwable().stackTrace[1] else d.stackElement
            return ServiceException(d.code, stackElement)
        }
    }
}

data class ControllerErrorResponse(
    val statusCode: Int,
    val message: String,
    val detail: String?
) {}


class ControllerException private constructor(
    code: ErrorCode,
    stackElement: StackTraceElement,
    private val isDebug: Boolean
) : LayerException(code, stackElement) {

    companion object {
        fun asControllerException(s: ServiceException, isDebug: Boolean = false): ControllerException {
            return ControllerException(s.code, s.stackElement, isDebug)
        }
    }

    val response: ControllerErrorResponse
        get() = ControllerErrorResponse(
            this.code.statusCode,
            this.code.message,
            if (this.isDebug) stackElement.toString() else null
        )

}