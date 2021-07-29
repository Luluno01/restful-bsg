package cyou.untitled.restfulbsg.commands.main

import cyou.untitled.bungeesafeguard.commands.subcommands.Subcommand
import cyou.untitled.bungeesafeguard.helpers.RedirectedLogger
import cyou.untitled.restfulbsg.RESTfulBSG
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import net.md_5.bungee.api.CommandSender

@Suppress("MemberVisibilityCanBePrivate")
open class ReloadCommand(override val context: RESTfulBSG) : Subcommand(context,"reload") {
    @Suppress("DEPRECATION")
    override fun execute(sender: CommandSender, realArgs: Array<out String>) {
        GlobalScope.launch(context.executorService.asCoroutineDispatcher()) {
            val config = context.config
            config.reload(sender)
            val logger = RedirectedLogger.get(context, sender)
            logger.info("Restarting server")
            val server = context.server
            server.stop()
            server.startAsync(sender)
        }
    }
}