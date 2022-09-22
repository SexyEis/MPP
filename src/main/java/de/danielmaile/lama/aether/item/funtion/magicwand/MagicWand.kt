package de.danielmaile.lama.aether.item.funtion.magicwand

import de.danielmaile.lama.aether.inst
import de.danielmaile.lama.aether.util.getNearestObjectInSight
import de.danielmaile.lama.aether.util.setDataString
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Llama
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector
import kotlin.math.min
import kotlin.random.Random

const val SELECTED_SPELL_TAG = "aether_selected_spell"
private val soundBeamSounds = Sound.values().filter { !it.name.contains("MUSIC") }.toList()

class MagicWand(itemStack: ItemStack) {

    var currentSpell: Spell
        private set
    var itemStack = itemStack
        private set

    init {
        val spell = Spell.fromTag(itemStack)
        if (spell == null) {
            currentSpell = Spell.LLAMA_SPELL
            writeSpellNBT()
        } else {
            currentSpell = spell
        }
    }

    fun nextSpell() {
        currentSpell = currentSpell.next()
        writeSpellNBT()
    }

    private fun writeSpellNBT() {
        itemStack.setDataString(SELECTED_SPELL_TAG, currentSpell.name)
    }

    fun fire(player: Player) {
        val range = currentSpell.range
        val nearestObject = player.getNearestObjectInSight(range)
        val location = player.location

        var objectRange = Double.MAX_VALUE
        if (nearestObject is LivingEntity) {
            objectRange = location.distance(nearestObject.location)
            nearestObject.hit(player)
        } else if (nearestObject is Block) {
            objectRange = location.distance(nearestObject.location)
            nearestObject.hit()
        }

        fireParticleBeam(location, min(objectRange, 20.0), currentSpell.color)
    }

    private fun LivingEntity.hit(player: Player) {
        if (this !is Player) return

        when (currentSpell) {
            Spell.SOUND_BEAM -> {
                playSound(
                    getEyeLocation(), soundBeamSounds[Random.nextInt(soundBeamSounds.size)],
                    1f, Random.nextDouble(0.85, 1.15).toFloat()
                )
            }
            Spell.YEET_SPELL -> {
                velocity = velocity.add(player.location.direction.clone().add(Vector(0.0, 2.0, 0.0)))
            }
            else -> {}
        }

    }

    private fun Block.hit() {
        if (currentSpell != Spell.LLAMA_SPELL) return

        val llama = world.spawnEntity(location, EntityType.LLAMA) as Llama
        llama.isInvulnerable = true
        llama.velocity = llama.velocity.add(Vector(0.0, 1.0, 0.0))

        object : BukkitRunnable() {
            var counter = 0
            override fun run() {
                if (llama.isOnGround || counter >= 100) {
                    world.spawnParticle(Particle.EXPLOSION_HUGE, llama.location, 0)
                    world.playSound(llama.location, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1f, 1f)
                    cancel()
                    Bukkit.getScheduler().runTaskLater(inst(), Runnable { llama.remove() }, 5L)
                    return
                }

                counter += 5
            }
        }.runTaskTimer(inst(), 20L, 5L)
    }

    private fun fireParticleBeam(origin: Location, range: Double, color: Color) {
        val currentLocation = origin.clone()
        val dustOptions = Particle.DustOptions(color, 1f)
        while (origin.distance(currentLocation) <= range) {
            currentLocation.world.spawnParticle(Particle.REDSTONE, currentLocation, 0, dustOptions)
            currentLocation.add(origin.direction)
        }
    }
}