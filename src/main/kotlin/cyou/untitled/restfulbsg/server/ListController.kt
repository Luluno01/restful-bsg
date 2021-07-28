package cyou.untitled.restfulbsg.server

import cyou.untitled.bungeesafeguard.list.UUIDList
import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import kotlinx.serialization.Serializable
import java.util.*

/**
 * Control for `/whitelist` and `/blacklist`
 */
class ListController(routing: Routing, list: UUIDList) {
    companion object {
        @Serializable
        private data class ListInfo(val enabled: Boolean)

        @Serializable
        private data class MainListInfo(val enabled: Boolean, val main: List<String>) {
            constructor(enabled: Boolean, main: Set<UUID>) : this(enabled, main.mapTo(mutableListOf()) { it.toString() })
        }

        @Serializable
        private data class LazyListInfo(val enabled: Boolean, val lazy: List<String>) {
            constructor(enabled: Boolean, lazy: Set<String>) : this(enabled, lazy.toTypedArray().toList())
        }
    }

    init {
        suspend fun onUpdateList(ctx: PipelineContext<Unit, ApplicationCall>) {
            val info = try {
                ctx.call.receive<ListInfo>()
            } catch (err: Throwable) {
                return ctx.call.respond400("Invalid list info")
            }
            if (info.enabled) {
                ctx.call.respond(list.on(null))
            } else {
                ctx.call.respond(list.off(null))
            }
        }
        routing
            .route("/${list.name}") {
                // Read full list
                get { call.respond(MainListInfo(list.enabled, list.get())) }
                // Update on/off state
                put { onUpdateList(this) }
                post { onUpdateList(this) }
                patch { onUpdateList(this) }
            }
            .route("/{user}") {
                // Add one UUID
                post {
                    val id = call.getUUIDOr40x() ?: return@post
                    call.respond(list.add(id))
                }
                // Remove one UUID
                delete {
                    val id = call.getUUIDOr40x() ?: return@delete
                    call.respond(list.remove(id))
                }
                // Check one UUID
                get {
                    val id = call.getUUIDOr40x() ?: return@get
                    call.respond(list.has(id))
                }
            }

        routing
            .route("/${list.lazyName}") {
                // Read full lazy list
                get { call.respond(LazyListInfo(list.enabled, list.lazyGet())) }
                // Update on/off state
                put { onUpdateList(this) }
                post { onUpdateList(this) }
                patch { onUpdateList(this) }
            }
            .route("/{username}") {
                // Add one username
                post {
                    val username = call.getUsernameOr400() ?: return@post
                    call.respond(list.lazyAdd(username))
                }
                // Remove one username
                delete {
                    val username = call.getUsernameOr400() ?: return@delete
                    call.respond(list.lazyRemove(username))
                }
                // Check one username
                get {
                    val username = call.getUsernameOr400() ?: return@get
                    call.respond(list.lazyHas(username))
                }
            }
    }
}