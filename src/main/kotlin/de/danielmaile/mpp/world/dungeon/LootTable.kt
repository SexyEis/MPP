/*
 * This file is part of MPP.
 * Copyright (c) 2022-2023 by it's authors. All rights reserved.
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

package de.danielmaile.mpp.world.dungeon

import de.danielmaile.mpp.item.ItemType
import org.bukkit.Material
import java.util.Random

enum class LootTable(
    val weight: Double,
    val minItemStacks: Int,
    val maxItemStacks: Int,
    private vararg val lootList: Loot
) {

    NORMAL(
        0.7, 3, 8,
        Loot(0.6, null, ItemType.AETHER_STONE, 3, 16),
        Loot(0.6, null, ItemType.AETHER_STICK, 1, 5),
        Loot(0.4, null, ItemType.AETHER_WOODEN_PICKAXE, 1, 2),
        Loot(0.4, null, ItemType.AETHER_WOODEN_SHOVEL, 1, 2),
        Loot(0.4, null, ItemType.AETHER_WOODEN_AXE, 1, 2),
        Loot(0.3, Material.BREAD, null, 1, 30),
        Loot(0.3, Material.COOKED_BEEF, null, 1, 12),
        Loot(0.6, Material.STONE, null, 1, 14),
        Loot(0.6, Material.STICK, null, 1, 13),
        Loot(0.4, null, ItemType.AETHER_WOODEN_SWORD, 1, 2),
        Loot(0.4, null, ItemType.AETHER_STONE_SWORD, 1, 2),
        Loot(0.4, null, ItemType.AETHER_STONE_PICKAXE, 1, 2),
        Loot(0.4, null, ItemType.AETHER_STONE_SHOVEL, 1, 2),
        Loot(0.4, Material.IRON_HELMET, null, 1, 2),
        Loot(0.4, Material.IRON_CHESTPLATE, null, 1, 1),
        Loot(0.4, Material.IRON_LEGGINGS, null, 1, 1),
        Loot(0.4, Material.IRON_BOOTS, null, 1, 1),
        Loot(0.4, Material.DIAMOND_CHESTPLATE, null, 1, 1)
    ),
    RARE(
        0.1, 1, 2,
        Loot(0.6, null, ItemType.AETHER_STONE, 3, 16),
        Loot(0.6, null, ItemType.AETHER_STICK, 1, 5),
        Loot(0.4, null, ItemType.AETHER_WOODEN_PICKAXE, 1, 2),
        Loot(0.4, null, ItemType.AETHER_WOODEN_SHOVEL, 1, 2),
        Loot(0.4, null, ItemType.AETHER_WOODEN_AXE, 1, 2),
        Loot(0.3, Material.BREAD, null, 1, 30),
        Loot(0.3, Material.COOKED_BEEF, null, 1, 12),
        Loot(0.6, Material.STONE, null, 1, 14),
        Loot(0.6, Material.STICK, null, 1, 13),
        Loot(0.4, null, ItemType.AETHER_WOODEN_SWORD, 1, 2),
        Loot(0.4, null, ItemType.AETHER_STONE_SWORD, 1, 2),
        Loot(0.4, null, ItemType.AETHER_STONE_PICKAXE, 1, 2),
        Loot(0.4, null, ItemType.AETHER_STONE_SHOVEL, 1, 2),
        Loot(0.4, Material.IRON_HELMET, null, 1, 2),
        Loot(0.4, Material.IRON_CHESTPLATE, null, 1, 1),
        Loot(0.4, Material.IRON_LEGGINGS, null, 1, 1),
        Loot(0.4, Material.IRON_BOOTS, null, 1, 1),
        Loot(0.4, Material.DIAMOND_CHESTPLATE, null, 1, 1),
        Loot(0.3, null, ItemType.ZANITE_STONE, 1, 2),
        Loot(0.2, null, ItemType.GRAVITITE_ORE, 1, 2),
        Loot(0.05, null, ItemType.FIRE_ESSENCE, 1, 1),
        Loot(0.05, null, ItemType.ICE_ESSENCE, 1, 1),
        Loot(0.05, null, ItemType.LIGHTNING_ESSENCE, 1, 1)
    );

    private fun getTotalLootWeight(): Double {
        var totalWeight = 0.0
        for (loot in lootList) {
            totalWeight += loot.weight
        }
        return totalWeight
    }

    fun getRandomLoot(random: Random): Loot {
        val totalWeight = getTotalLootWeight()
        var index = 0
        var randomWeight = random.nextDouble() * totalWeight

        while (index < lootList.size - 1) {
            randomWeight -= lootList[index].weight
            if (randomWeight <= 0.0) break
            index++
        }

        return lootList[index]
    }

    companion object {

        fun getTotalWeight(): Double {
            var totalWeight = 0.0
            for (table in values()) {
                totalWeight += table.weight
            }
            return totalWeight
        }
    }

    data class Loot(
        val weight: Double,
        val material: Material?,
        val itemType: ItemType?,
        val minAmount: Int,
        val maxAmount: Int
    ) {
        fun isCustomItem(): Boolean {
            return itemType != null
        }
    }
}
