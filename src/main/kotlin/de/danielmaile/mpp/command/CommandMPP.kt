/*
 * This file is part of MPP.
 * Copyright (c) 2022 by it's authors. All rights reserved.
 * MPP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MPP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MPP.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.danielmaile.mpp.command

import de.danielmaile.mpp.aetherWorld
import de.danielmaile.mpp.data.config.LanguageManager
import de.danielmaile.mpp.gui.ItemCollectionGUI
import de.danielmaile.mpp.inst
import de.danielmaile.mpp.item.ItemType
import de.danielmaile.mpp.mob.MPPMob
import de.danielmaile.mpp.util.getDirection
import de.danielmaile.mpp.util.isLong
import de.danielmaile.mpp.world.dungeon.DungeonChest
import net.kyori.adventure.text.Component
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil
import java.util.Locale
import java.util.Random
import java.util.stream.Stream

class CommandMPP : CommandExecutor, TabCompleter {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val languageManager = inst().getLanguageManager()
        val cmdPrefix = languageManager.getComponent("messages.prefix")

        if (sender !is Player) {
            sender.sendMessage(cmdPrefix.append(languageManager.getComponent("messages.cmd.errors.only_player_cmd")))
            return true
        }

        when (args.size) {
            1 -> {
                when (args[0].lowercase()) {
                    "chest" -> {
                        val facing = sender.getDirection()
                        DungeonChest(Random()).instantiate(
                            sender.location.block.getRelative(facing).location,
                            sender.getDirection().oppositeFace
                        )
                    }

                    "collection" -> {
                        collectionGUI(sender)
                    }

                    "reload" -> {
                        inst().reloadConfig()
                        sender.sendMessage(cmdPrefix.append(languageManager.getComponent("messages.cmd.info.reloaded_config")))
                    }

                    "teleport" -> {
                        teleportCMD(sender)
                    }

                    else -> {
                        sendHelp(sender, languageManager, cmdPrefix)
                    }
                }
            }

            2 -> {
                when (args[0].lowercase()) {
                    "give" -> {
                        giveCMD(sender, args, languageManager, cmdPrefix)
                    }

                    else -> {
                        sendHelp(sender, languageManager, cmdPrefix)
                    }
                }
            }

            3 -> {
                when (args[0].lowercase()) {
                    "summon" -> {
                        summonCMD(sender, args, languageManager, cmdPrefix)
                    }

                    else -> {
                        sendHelp(sender, languageManager, cmdPrefix)
                    }
                }
            }

            else -> sendHelp(sender, languageManager, cmdPrefix)
        }
        return true
    }

    private fun collectionGUI(player: Player) {
        ItemCollectionGUI().open(player)
    }

    private fun summonCMD(
        player: Player,
        args: Array<out String>,
        languageManager: LanguageManager,
        cmdPrefix: Component
    ) {
        val mob: MPPMob

        try {
            mob = MPPMob.valueOf(args[1].uppercase(Locale.getDefault()))
        } catch (exception: IllegalArgumentException) {
            player.sendMessage(
                cmdPrefix
                    .append(languageManager.getComponent("messages.cmd.errors.mob_does_not_exist"))
            )
            return
        }

        if (!args[2].isLong()) {
            player.sendMessage(
                cmdPrefix
                    .append(languageManager.getComponent("messages.cmd.errors.value_no_integer"))
            )
            return
        }

        val level = java.lang.Long.parseLong(args[2])
        mob.summon(player.location, level)
    }

    private fun teleportCMD(player: Player) {
        player.teleport(aetherWorld().spawnLocation)
    }

    private fun giveCMD(
        player: Player,
        args: Array<out String>,
        languageManager: LanguageManager,
        cmdPrefix: Component
    ) {
        try {
            val item = ItemType.valueOf(args[1].uppercase(Locale.getDefault()))
            player.inventory.addItem(item.getItemStack())
        } catch (exception: IllegalArgumentException) {
            player.sendMessage(
                cmdPrefix
                    .append(languageManager.getComponent("messages.cmd.errors.item_does_not_exist"))
            )
        }
    }

    private fun sendHelp(player: Player, languageManager: LanguageManager, cmdPrefix: Component) {
        for (component in languageManager.getComponentList("messages.cmd.info.help_text")) {
            player.sendMessage(cmdPrefix.append(component))
        }
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): MutableList<String>? {
        if (sender !is Player) {
            return null
        }

        val tabComplete: MutableList<String> = ArrayList()
        val completions: ArrayList<String> = ArrayList()

        if (args.size == 1) {
            tabComplete.add("chest")
            tabComplete.add("collection")
            tabComplete.add("give")
            tabComplete.add("reload")
            tabComplete.add("summon")
            tabComplete.add("teleport")
            StringUtil.copyPartialMatches(args[0], tabComplete, completions)
        }

        if (args.size == 2 && args[0].equals("give", ignoreCase = true)) {
            // get item names from enum
            val itemNames = Stream.of(*ItemType.values()).map { obj: ItemType -> obj.name }
                .toList()
            tabComplete.addAll(itemNames)
            StringUtil.copyPartialMatches(args[1], tabComplete, completions)
        }

        if (args.size == 2 && args[0].equals("summon", ignoreCase = true)) {
            // get mob names from enum
            val itemNames = Stream.of(*MPPMob.values()).map { obj: MPPMob -> obj.name }
                .toList()
            tabComplete.addAll(itemNames)
            StringUtil.copyPartialMatches(args[1], tabComplete, completions)
        }

        completions.sort()
        return completions
    }
}
