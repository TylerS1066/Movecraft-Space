package io.github.eirikh1996.movecraftspace.commands

import io.github.eirikh1996.movecraftspace.MovecraftSpace
import io.github.eirikh1996.movecraftspace.expansion.ExpansionManager
import io.github.eirikh1996.movecraftspace.expansion.ExpansionState
import io.github.eirikh1996.movecraftspace.utils.MSUtils.COMMAND_PREFIX
import io.github.eirikh1996.movecraftspace.utils.MSUtils.ERROR
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.Bukkit.*
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

object MovecraftSpaceCommand : TabExecutor {
    override fun onCommand(sender : CommandSender, cmd : Command, label : String, args : Array<out String>): Boolean {
        if (!cmd.name.equals("movecraftspace", true))
            return false
        if (args.size == 0) {
            sender.sendMessage(COMMAND_PREFIX + "Version : " + MovecraftSpace.instance.description.version)
        } else if (args[0].equals("expansions", true)) {
            if (args.size <= 1) {
                val expansionList = TextComponent(COMMAND_PREFIX + "Expansions loaded: ")
                var index = 0
                val loadedExpansions = ExpansionManager.filter { e -> e.state != ExpansionState.NOT_LOADED }
                for (ex in loadedExpansions) {
                    index++
                    var entry = ""
                    entry += if (ex.state == ExpansionState.ENABLED) {
                        "§a"
                    } else {
                        "§c"
                    }
                    entry += ex.name
                    entry += "§r"
                    val tp = TextComponent(entry)
                    tp.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("Click for expansion information"))
                    tp.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/movecraftspace expansion " + ex.name)
                    expansionList.addExtra(tp)
                    if (index >= loadedExpansions.size)
                        expansionList.addExtra(", ")
                }
                sender.spigot().sendMessage(expansionList)
                return true
            }
            val expansion = ExpansionManager.getExpansion(args[1])
            if (expansion == null) {
                sender.sendMessage(COMMAND_PREFIX + ERROR + "No such expansion is loaded: " + args[1])
                return true
            }
            sender.sendMessage("===========[" + expansion.name + "]===========")
            sender.sendMessage("Description: " + expansion.description)
            sender.sendMessage("Version: " + expansion.version)
            sender.sendMessage("Depend: " + expansion.depend.joinToString())
            sender.sendMessage("Soft depend: " + expansion.softdepend.joinToString())
            sender.sendMessage("Expansion depend: " + expansion.expansionDepend.joinToString())
            sender.sendMessage("Expansion soft depend: " + expansion.expansionSoftDepend.joinToString())
        }
        return true
    }

    override fun onTabComplete(sender : CommandSender, cmd : Command, label : String, args : Array<out String>) : List<String> {
        val tabCompletions = listOf("expansions").sorted()
        if (args.size == 0)
            return tabCompletions
        val completions = ArrayList<String>()
        for (c in tabCompletions) {
            if (!c.startsWith(args[args.size - 1]))
                continue
            completions.add(c)
        }
        completions.sort()
        return completions;
    }
}