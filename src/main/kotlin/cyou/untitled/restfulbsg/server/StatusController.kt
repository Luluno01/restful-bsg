package cyou.untitled.restfulbsg.server

import cyou.untitled.bungeesafeguard.BungeeSafeguard
import cyou.untitled.bungeesafeguard.Config
import cyou.untitled.bungeesafeguard.list.UUIDList
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
        private data class Status(val configInUse: String, val backend: String, val listStates: Map<String, Boolean>) {
            constructor(config: Config, backend: Backend, lists: List<UUIDList>) : this(
                config.configInUse,
                backend.toString(),
                lists.mapTo(mutableListOf()) { Pair(it.name, it.enabled) }.toMap()
            )
        }
    }

    init {
        routing {
            get("/status") {
                val bsg = BungeeSafeguard.getPlugin()
                call.respond(Status(bsg.config, Backend.getBackend(), bsg.listMgr.lists))
            }
        }
    }
}