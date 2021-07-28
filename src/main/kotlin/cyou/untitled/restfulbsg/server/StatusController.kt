package cyou.untitled.restfulbsg.server

import cyou.untitled.bungeesafeguard.BungeeSafeguard
import cyou.untitled.bungeesafeguard.Config
import cyou.untitled.bungeesafeguard.storage.Backend
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.serialization.Serializable

/**
 * Controller for `/status`
 */
class StatusController(routing: Routing) {
    companion object {
        @Serializable
        private data class Status(val configInUse: String, val backend: String) {
            constructor(config: Config, backend: Backend): this(config.configInUse, backend.toString())
        }
    }
    init {
        routing {
            get("/status") {
                call.respond(Status(BungeeSafeguard.getPlugin().config, Backend.getBackend()))
            }
        }
    }
}