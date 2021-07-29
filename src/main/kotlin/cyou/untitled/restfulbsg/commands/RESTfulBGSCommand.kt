package cyou.untitled.restfulbsg.commands

import cyou.untitled.bungeesafeguard.commands.subcommands.SubcommandRegistry
import cyou.untitled.restfulbsg.RESTfulBSG
import cyou.untitled.restfulbsg.commands.main.ReloadCommand
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.plugin.Command

@Suppress("MemberVisibilityCanBePrivate")
open class RESTfulBGSCommand(context: RESTfulBSG): Command("restful-bsg", "restfulbsg.main", "restfulbsg", "restbsg") {
    companion object {
        open class Usage: SubcommandRegistry.Companion.UsageSender {
            override fun sendUsage(sender: CommandSender) {
                sender.sendMessage(TextComponent("${ChatColor.YELLOW}Usage:"))
                sender.sendMessage(TextComponent("${ChatColor.AQUA}  /restful-bsg reload"))
            }
        }
    }
    protected val cmdReg = SubcommandRegistry(context, Usage())

    init {
        cmdReg.registerSubcommand(ReloadCommand(context))
    }

    override fun execute(sender: CommandSender, args: Array<out String>) {
        cmdReg.getSubcommand(sender, args)?.execute(sender, args.sliceArray(IntRange(0, args.size - 1)))
    }
}