package gameplugin.gameplugin

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Chest
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import kotlin.random.Random
import kotlin.random.nextInt

object Util {

    fun aooniselect(oni: Player){
        scoreboard.getTeam("aooni")?.addEntry(oni.name)
        oni.teleport(Location(Bukkit.getWorld("world"), -10.0, 51.0, 77.0))
        val huwadama = ItemStack(Material.SLIME_BALL)
        val huwadamameta = huwadama.itemMeta
        huwadamameta.setDisplayName("§9フワ玉")
        huwadama.itemMeta = huwadamameta
        oni.inventory.clear()
        oni.inventory.setItem(4, huwadama)
        oni.foodLevel = 0
        oni.inventory.helmet = ItemStack(Material.DIAMOND_HELMET)
        oni.inventory.chestplate = ItemStack(Material.DIAMOND_CHESTPLATE)
        oni.inventory.leggings = ItemStack(Material.DIAMOND_LEGGINGS)
        oni.inventory.boots = ItemStack(Material.DIAMOND_BOOTS)

        for (p in Bukkit.getOnlinePlayers()) {
            p.sendMessage(prefix + "§9" + (oni.name) + "が青鬼に選ばれました")
        }
        oni.world.getBlockAt(-10, 51, 75).type = Material.LAPIS_BLOCK
        oni.world.getBlockAt(44, 51, 43).type = Material.AIR
        oni.world.getBlockAt(-1, 69, 39).type = Material.AIR
        oni.world.getBlockAt(-4,74,78).type = Material.AIR
        object : BukkitRunnable() {
            override fun run() {
                oni.world.getBlockAt(-4,73,78).type = Material.REDSTONE_BLOCK
            }
        }.runTaskLater(plugin,20)

        setitem(oni)


    }

    private fun setitem(oni: Player){
        val chest = ArrayList<Chest>()
        val plate = ArrayList<Chest>()
        val wool = ArrayList<Chest>()
        chest.add(oni.world.getBlockAt(-31, 51, 105).state as Chest)
        chest.add(oni.world.getBlockAt(-13, 51, 57).state as Chest)
        chest.add(oni.world.getBlockAt(-47, 51, 69).state as Chest)
        chest.add(oni.world.getBlockAt(49, 53, 66).state as Chest)
        chest.add(oni.world.getBlockAt(66, 49, 77).state as Chest)
        chest.add(oni.world.getBlockAt(-26, 51, 81).state as Chest)

        chest.add(oni.world.getBlockAt(14, 57, 70).state as Chest)
        chest.add(oni.world.getBlockAt(20, 57, 45).state as Chest)
        chest.add(oni.world.getBlockAt(-5, 57, 71).state as Chest)
        chest.add(oni.world.getBlockAt(-22, 57, 31).state as Chest)
        chest.add(oni.world.getBlockAt(-26, 56, 60).state as Chest)

        chest.add(oni.world.getBlockAt(13, 62, 53).state as Chest)

        chest.add(oni.world.getBlockAt(-5, 69, 47).state as Chest)

        plate.addAll(chest)

        chest.add(oni.world.getBlockAt(47, 45, 70).state as Chest)
        chest.add(oni.world.getBlockAt(16, 45, 54).state as Chest)
        chest.add(oni.world.getBlockAt(32, 47, 43).state as Chest)
        chest.add(oni.world.getBlockAt(-16, 44, -7).state as Chest)
        chest.add(oni.world.getBlockAt(45, 45, 4).state as Chest)
        chest.add(oni.world.getBlockAt(-28,43,-23).state as Chest)

        wool.add(oni.world.getBlockAt(47, 45, 70).state as Chest)
        wool.add(oni.world.getBlockAt(16, 45, 54).state as Chest)
        wool.add(oni.world.getBlockAt(32, 47, 43).state as Chest)
        wool.add(oni.world.getBlockAt(-16, 44, -7).state as Chest)
        wool.add(oni.world.getBlockAt(45, 45, 4).state as Chest)
        wool.add(oni.world.getBlockAt(-28,43,-23).state as Chest)

        for (c in chest){
            c.inventory.clear()
        }
        for (w in wool){
            w.inventory.clear()
        }
        val plass = plate[Random.nextInt(plate.size)].inventory
        for (s in 0..26){
            plass.setItem(s, ItemStack(Material.OAK_PRESSURE_PLATE))
        }
        val set = wool[Random.nextInt(wool.size)].inventory
        for (s in 0..26){
            set.setItem(s, ItemStack(Material.BLUE_WOOL))
        }
        for (p in 0 until chest.size){
            val r = Random.nextInt(2..4)
            for (s in 1..r){
                chest[p].inventory.setItem(Random.nextInt(0..26), ItemStack(Material.COOKED_PORKCHOP))
            }
        }
    }



}