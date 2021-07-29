package cyou.untitled.restfulbsg

import cyou.untitled.bungeesafeguard.BungeeSafeguard
import cyou.untitled.bungeesafeguard.helpers.DependencyFixer
import cyou.untitled.restfulbsg.commands.RESTfulBGSCommand
import cyou.untitled.restfulbsg.server.Server
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.runBlocking
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.plugin.Plugin
import org.bstats.bungeecord.Metrics

class RESTfulBSG: Plugin() {
    companion object {
        init {
            DependencyFixer.fixLibraryLoader(RESTfulBSG::class.java.classLoader)
        }
    }

    val config = Config(this)
    val server = Server(this)

    @Suppress("DEPRECATION")
    override fun onEnable() {
        Metrics(this, 11865)
        runBlocking(executorService.asCoroutineDispatcher()) {
            config.saveDefaultConfig()
            config.load(null)
        }
        val pluginManager = proxy.pluginManager
        val bsg = pluginManager.getPlugin("BungeeSafeguard") as BungeeSafeguard
        if (bsg.enabled) server.startAsync(null)
        proxy.pluginManager.registerCommand(this, RESTfulBGSCommand(this))
        logger.info("${ChatColor.GREEN}RESTful-BSG enabled")
    }

    @Suppress("DEPRECATION")
    override fun onDisable() {
        proxy.pluginManager.unregisterCommands(this)
        logger.info("Try stopping RESTful server")
        runBlocking(executorService.asCoroutineDispatcher()) {
            server.stop()
        }
        logger.info("RESTful-BSG disabled")
    }
}