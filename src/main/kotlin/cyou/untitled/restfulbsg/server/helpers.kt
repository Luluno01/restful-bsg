package cyou.untitled.restfulbsg.server

import cyou.untitled.bungeesafeguard.BungeeSafeguard
import cyou.untitled.bungeesafeguard.helpers.UserNotFoundException
import cyou.untitled.bungeesafeguard.helpers.UserUUIDHelper
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import java.util.*

suspend fun ApplicationCall.respond400(message: String) {
    respondText(message, status = HttpStatusCode.BadRequest)
}

suspend fun ApplicationCall.respond404(message: String) {
    respondText(message, status = HttpStatusCode.NotFound)
}

suspend fun ApplicationCall.getParamOr400(key: String, name: String): String? {
    val param = parameters[key]
    if (param == null) {
        respond400("Missing $name")
        return null
    }
    return param
}

val ApplicationCall.xbox: Boolean
    get() = when (request.queryParameters["xbox"]) {
        "true", "1" -> true
        else -> false
    }

suspend fun ApplicationCall.getUUIDOr40x(): UUID? {
    val rawUser = getParamOr400("user", "UUID or username") ?: return null
    var id: UUID? = null
    UserUUIDHelper.resolveUUIDs(BungeeSafeguard.getPlugin(), arrayOf(rawUser), this.xbox) {
        id = when (it.err) {
            null -> {
                it.result!!.id
            }
            is UserNotFoundException -> {
                respond404("User name not found or invalid UUID")
                null
            }
            else -> {
                respond400("Invalid UUID or username")
                null
            }
        }
    }
    return id
}

suspend fun ApplicationCall.getUsernameOr400(): String? {
    val username = getParamOr400("username", "username") ?: return null
    if (username.trim().isEmpty()) {
        respond400("Invalid username")
        return null
    }
    return username
}