package cyou.untitled.restfulbsg.server

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import java.util.*

suspend fun ApplicationCall.respond400(message: String) {
    respondText(message, status = HttpStatusCode.BadRequest)
}

suspend fun ApplicationCall.getParamOr400(key: String, name: String): String? {
    val param = parameters[key]
    if (param == null) {
        respond400("Missing $name")
        return null
    }
    return param
}

suspend fun ApplicationCall.getUUIDOr400(): UUID? {
    val rawId = getParamOr400("id", "UUID") ?: return null
    return try {
        UUID.fromString(rawId)
    } catch (err: IllegalArgumentException) {
        respond400("Invalid UUID")
        null
    }
}

suspend fun ApplicationCall.getUsernameOr400(): String? {
    val username = getParamOr400("username", "username") ?: return null
    if (username.trim().isEmpty()) {
        respond400("Invalid username")
        return null
    }
    return username
}