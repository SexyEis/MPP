package de.danielmaile.lama.aether.util

import de.danielmaile.lama.aether.inst
import de.danielmaile.lama.aether.item.ArmorSet
import de.danielmaile.lama.aether.item.ItemType
import de.danielmaile.lama.lamaapi.LamaAPI
import org.bukkit.FluidCollisionMode
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.io.IOException
import java.io.InputStream

fun logInfo(message: String) {
    inst().logger.info(message)
}

fun logError(message: String) {
    inst().logger.severe(message)
}

@Throws(IOException::class)
fun getResource(fileName: String): InputStream? {
    return inst().javaClass.classLoader.getResourceAsStream(fileName)
}

fun ItemStack.doesKeyExist(key: String): Boolean {
    val namespacedKey = NamespacedKey(inst(), key)
    return LamaAPI.ItemData.doesKeyExist(this, namespacedKey)
}

fun ItemStack.getDataString(key: String): String? {
    val namespacedKey = NamespacedKey(inst(), key)
    return LamaAPI.ItemData.getData(this, namespacedKey, PersistentDataType.STRING)
}

fun ItemStack.setDataString(key: String, value: String) {
    val namespacedKey = NamespacedKey(inst(), key)
    LamaAPI.ItemData.setData(this, namespacedKey, PersistentDataType.STRING, value)
}

fun Player.isGrounded(): Boolean {
    return this.getBlockBelow().type != Material.AIR
}

fun Player.getBlockBelow(): Block {
    return location.block.getRelative(BlockFace.DOWN)
}

fun Player.setMaximumHealth(value: Double) {
    getAttribute(Attribute.GENERIC_MAX_HEALTH)?.baseValue = value
}

fun Player.addPermEffect(type: PotionEffectType) {
    addPotionEffect(PotionEffect(type, Int.MAX_VALUE, 0, false, false))
}

/**
 * @return nearest object (block or entity) in sight of player or null if none was found
 */
fun Player.getNearestObjectInSight(range: Int): Any? {
    val block = getTargetBlockExact(range, FluidCollisionMode.NEVER)
    val entity = getTargetEntity(range)

    val blockRange = if (block != null) location.distance(block.location) else Double.MAX_VALUE
    val entityRange = if (entity != null) location.distance(entity.location) else Double.MAX_VALUE

    return if (entity == null && block == null) null else (if (blockRange < entityRange) block else entity)
}

fun Player.getEquippedArmorSet(): ArmorSet? {
    val headType = ItemType.fromTag(this.equipment.helmet)
    val chestType = ItemType.fromTag(this.equipment.chestplate)
    val legsType = ItemType.fromTag(this.equipment.leggings)
    val feetType = ItemType.fromTag(this.equipment.boots)

    return ArmorSet.values().firstOrNull {
        headType == it.head && chestType == it.chest && legsType == it.legs && feetType == it.feet
    }
}

//Returns the block face of the direction the player is facing (only yaw)
fun Player.getDirection(): BlockFace {
    val yaw = this.location.yaw
    return if (yaw > 135 || yaw <= -135) BlockFace.NORTH else if (yaw > -135 && yaw <= -45) BlockFace.EAST else if (yaw > -45 && yaw <= 45) BlockFace.SOUTH else BlockFace.WEST
}

fun Block.isSurroundedByAirOrMaterial(allowedMaterials: Set<Material>): Boolean {
    for (blockFace in BlockFace.values().filter { it.isCartesian }) {
        val relativeMaterial = this.getRelative(blockFace).type
        if (relativeMaterial != Material.AIR && !allowedMaterials.contains(relativeMaterial)) return false
    }
    return true
}