package cyou.untitled.restfulbsg.server

import cyou.untitled.bungeesafeguard.BungeeSafeguard
import cyou.untitled.bungeesafeguard.helpers.RedirectedLogger
import cyou.untitled.restfulbsg.RESTfulBSG
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import net.md_5.bungee.api.CommandSender

class Server(private val context: RESTfulBSG) {
    private val lock = Mutex()
    private var app: ApplicationEngine? = null
    private val config = context.config
    private val proxy = context.proxy
    @Suppress("DEPRECATION")
    private val executorService = context.executorService

    @Suppress("MemberVisibilityCanBePrivate")
    suspend fun start(sender: CommandSender?) {
        lock.withLock {
            if (app != null) return
            val host = config.host.ifBlank { "127.0.0.1" }
            val port = config.port
            val logger = RedirectedLogger.get(context, sender)
            if (!BungeeSafeguard.getPlugin().enabled) {
                logger.severe("BungeeSafeguard is not enabled, refuse to start the server")
                return@withLock
            }
            if (host == "0.0.0.0" || host == "::") {
                logger.warning(
                    "You are exposing public access to the whitelist/blacklist without authentication!" +
                            " If this is not your intention, stop the server immediately and change \"host\" in the" +
                            " config to \"127.0.0.1\" or something appropriate.")
            }
            logger.info("Starting RESTful server @ $host:$port")
            try {
                app = embeddedServer(Netty, host = host, port = port.toInt()) {
                    install(ContentNegotiation) {
                        json()
                    }

                    val bsg = proxy.pluginManager.getPlugin("BungeeSafeguard") as BungeeSafeguard
                    val whitelist = bsg.whitelist
                    val blacklist = bsg.blacklist

                    routing {
                        StatusController(this)
                        ListController(this, whitelist)
                        ListController(this, blacklist)
                    }
                }.start(wait = false)
            } catch (err: Throwable) {
                app?.stop(2000L, 3000L)
                app = null
                logger.severe("Cannot start the server: $err")
                err.printStackTrace()
                logger.severe("Please check the config and reload")
            }
        }
    }

    fun startAsync(sender: CommandSender?) {
        proxy.scheduler.runAsync(context) {
            runBlocking(executorService.asCoroutineDispatcher()) {
                start(sender)
            }
        }
    }

    suspend fun stop() {
        lock.withLock {
            app?.stop(2000L, 3000L)
            app = null
        }
    }
}