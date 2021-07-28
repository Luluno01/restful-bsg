package cyou.untitled.restfulbsg

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.plugin.Plugin
import java.io.File
import java.nio.file.Files
import cyou.untitled.bungeesafeguard.helpers.RedirectedLogger
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.config.Configuration
import net.md_5.bungee.config.ConfigurationProvider
import net.md_5.bungee.config.YamlConfiguration

class Config(private val context: Plugin) {
    companion object {
        const val DEFAULT_CONFIG = "config.yml"

        const val HOST = "host"
        const val PORT = "port"
    }

    private val lock = Mutex()

    private val dataFolder: File
        get() = context.dataFolder

    @Volatile
    var host: String = ""
        private set
    @Volatile
    var port: UShort = 0u
        private set

    @Suppress("BlockingMethodInNonBlockingContext")
    fun saveDefaultConfig(name: String = DEFAULT_CONFIG) {
        if (!dataFolder.exists()) {
            dataFolder.mkdirs()
        }
        val conf = File(dataFolder, name)
        if (!conf.exists()) {
            context.getResourceAsStream(DEFAULT_CONFIG).use { input -> Files.copy(input, conf.toPath()) }
        }
    }

    private suspend fun <T> withLock(owner: Any? = null, action: suspend () -> T): T {
        lock.withLock(owner) {
            return action()
        }
    }

    suspend fun load(sender: CommandSender?, configName: String = DEFAULT_CONFIG) {
        withLock {
            val logger = RedirectedLogger.get(context, sender)
            logger.info("Loading config file ${ChatColor.AQUA}$configName")
            val conf = loadConfigFromFile(configName)
            host = conf.getString(HOST)
            port = conf.getInt(PORT).toUShort()
            logger.info("Config loaded")
        }
    }

    suspend fun reload(sender: CommandSender?, configName: String = DEFAULT_CONFIG) = load(sender, configName)

    private fun loadConfigFromFile(configName: String): Configuration {
        return ConfigurationProvider.getProvider(YamlConfiguration::class.java)
            .load(File(dataFolder, configName))
    }
}