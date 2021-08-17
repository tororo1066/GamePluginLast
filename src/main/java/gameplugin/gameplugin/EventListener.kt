package gameplugin.gameplugin

import gameplugin.gameplugin.GameCommand.mode
import gameplugin.gameplugin.GameCommand.umatime
import gameplugin.gameplugin.Main.Companion.huwadamamenu
import org.bukkit.*
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.*
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.*
import org.bukkit.event.vehicle.VehicleDestroyEvent
import org.bukkit.event.vehicle.VehicleExitEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import kotlin.random.Random
import kotlin.random.nextInt


object EventListener : Listener {

    private var death = false
    private var bow = HashMap<Player, Boolean>()
    var huwadamause = false
    var cooltime = 60


    @EventHandler
    fun sign(e : SignChangeEvent){
        for (i in e.lines.indices){
            if (e.getLine(i) == null)continue
            e.setLine(i,e.getLine(i)?.replace("&","§"))
        }
    }

    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        if (!e.player.isOp && aoonistart){
            e.player.gameMode = GameMode.SURVIVAL
            scoreboard.getTeam("hukkatu")?.addEntry(e.player.name)
            e.player.health = 0.0
        }
        if (e.player.hasPlayedBefore()) {
            e.joinMessage = e.player.name + "§eがログインしました"
        } else {
            e.joinMessage = e.player.name + "§eがログインしました (初参加)"
        }
    }

    @EventHandler
    fun leave(e: PlayerQuitEvent){
        scoreboard.getTeam("aooni")?.removeEntry(e.player.name)
        scoreboard.getTeam("hiroshi")?.removeEntry(e.player.name)
        scoreboard.getTeam("hukkatu")?.removeEntry(e.player.name)
        scoreboard.getTeam("spectator")?.removeEntry(e.player.name)
        scoreboard.getTeam("goal")?.removeEntry(e.player.name)
    }

    @EventHandler
    fun build(e : BlockCanBuildEvent){
        if (!aoonistart)return
        if (e.block.location != Location(e.player?.world, 44.0, 51.0, 43.0))return
        if (e.block.location != Location(e.player?.world, -1.0, 69.0, 39.0))return
        e.isBuildable = false
    }

    @EventHandler
    fun opencheat(e : InventoryOpenEvent){
        if (e.inventory == huwadamamenu)return
        if (e.inventory.type == InventoryType.CHEST && scoreboard.getTeam("aooni")?.hasEntry(e.player.name)!!)e.isCancelled = true

    }


    @EventHandler
    fun onPlayerInteract(e: PlayerInteractEvent) {
        if (e.hand == EquipmentSlot.OFF_HAND)return
        if (!aoonistart)return

        if (e.action == Action.PHYSICAL && e.clickedBlock?.type == Material.ACACIA_PRESSURE_PLATE){
            if (e.material == Material.COOKED_PORKCHOP || !scoreboard.getTeam("aooni")?.hasEntry(e.player.name)!!)e.isCancelled = true
        }

        if (e.action == Action.RIGHT_CLICK_BLOCK && e.clickedBlock?.type == Material.PACKED_ICE){
            val age = e.clickedBlock?.blockData as Ageable
            e.clickedBlock?.breakNaturally(e.item)
            e.clickedBlock?.type = Material.WHEAT
            if (scoreboard.getTeam("aooni")?.hasEntry(e.player.name)!! || e.player.inventory.itemInMainHand.type == Material.DIAMOND) {

                e.player.teleport(Location(e.player.world, 4.0, 50.0, 12.0,180f,0f))


            }



        }


        if (e.action == Action.RIGHT_CLICK_BLOCK && e.clickedBlock?.type == Material.DIAMOND_BLOCK && e.player.inventory.itemInMainHand.type == Material.DIAMOND && scoreboard.getTeam("hiroshi")?.hasEntry(e.player.name)!!){

            e.player.teleport(Location(e.player.world, 4.0, 56.0, -71.0))

            scoreboard.getTeam("goal")?.addEntry(e.player.name)
                for (p in Bukkit.getOnlinePlayers()){
                    p.sendMessage(prefix + "§b${e.player.name}が青鬼の館から脱出しました")
                }
            if (scoreboard.getTeam("hiroshi")?.size == 0){
                time = 0
            }
        }
        if (e.action == Action.RIGHT_CLICK_BLOCK && e.clickedBlock?.type == Material.GOLD_ORE) {

                death = true
                Bukkit.dispatchCommand(e.player, "sneak")
                scoreboard.getTeam("hiroshi")?.addEntry(e.player.name)
                if (easy || mode[e.player] == "easy"){
                    zanki[e.player] = 2
                }
                e.player.health = 0.0
                for (p in Bukkit.getOnlinePlayers()) {
                    p.sendMessage(prefix + "§b${e.player.name}が復活しました")
                }
            }
        if (huwadamause) {

            if (e.action == Action.RIGHT_CLICK_AIR || e.action == Action.RIGHT_CLICK_BLOCK) {
                if (e.player.inventory.itemInMainHand.type == Material.SLIME_BALL) {
                    e.player.openInventory(huwadamamenu)
                }
            }
        }


    }

    @EventHandler
    fun onClick(e: InventoryClickEvent) {
        if (!aoonistart)return
        val player: Player = e.whoClicked as Player
        val item = e.currentItem ?:return
        if (item.type == Material.DIAMOND)e.isCancelled = true
        if (e.inventory == huwadamamenu) {
            when (e.slot) {
                6 -> {
                    cooltime = 60
                    huwadamause = false
                    object : BukkitRunnable() {
                        override fun run() {

                            if (cooltime == 1) {
                                huwadamause = true
                                cancel()
                            }
                            cooltime--
                        }
                    }.runTaskTimer(plugin, 0, 20)
                    object : BukkitRunnable() {
                        override fun run() {
                            player.teleport(Location(player.world, 25.0, 63.0, 40.0,180f,0f))
                        }
                    }.runTaskLater(plugin, 100)
                }
                2 -> {
                    cooltime = 60
                    huwadamause = false
                    object : BukkitRunnable() {
                        override fun run() {

                            if (cooltime == 1) {
                                huwadamause = true
                                cancel()
                            }
                            cooltime--
                        }
                    }.runTaskTimer(plugin, 0, 20)
                    object : BukkitRunnable() {
                        override fun run() {
                            player.teleport(Location(player.world, 15.0, 51.0, 82.0,180f,0f))
                        }
                    }.runTaskLater(plugin, 100)


                }



            }
                e.isCancelled = true
                e.whoClicked.closeInventory()
        }
        if (e.inventory.type == InventoryType.CHEST){
            if (player.inventory.contents.contains(ItemStack(Material.OAK_PRESSURE_PLATE)) || player.inventory.contents.contains(ItemStack(Material.OAK_PRESSURE_PLATE))) if(item.type == Material.BLUE_WOOL || item.type == Material.OAK_PRESSURE_PLATE)e.isCancelled = true
        }
    }

    @EventHandler
    fun damage(e: EntityDamageByEntityEvent){
        if (!aoonistart)return

        if (e.damager is Arrow && e.entity is Cow){

            val arrow = e.damager as Arrow
            val player = arrow.shooter as Player
            player.teleport(Location(player.world, -128.0, 52.0, -6.0))
            e.isCancelled = true
        }


        if (e.entityType == EntityType.PLAYER && e.damager.type == EntityType.PLAYER){
            val attackplayer = e.damager as Player
            val damager = e.entity as Player

            if (scoreboard.getTeam("hiroshi")?.hasEntry(attackplayer.name)!! && scoreboard.getTeam("hiroshi")?.hasEntry(damager.name)!!)e.isCancelled = true
            if (scoreboard.getTeam("hukkatu")?.hasEntry(attackplayer.name)!! && scoreboard.getTeam("hukkatu")?.hasEntry(damager.name)!!)e.isCancelled = true
            if (scoreboard.getTeam("aooni")?.hasEntry(attackplayer.name)!! && scoreboard.getTeam("hiroshi")?.hasEntry(damager.name)!! && taizai){
                e.damage = 20.0
                return
            }
            if (scoreboard.getTeam("aooni")?.hasEntry(attackplayer.name)!! && scoreboard.getTeam("hiroshi")?.hasEntry(damager.name)!! && !taizai && 74.0 < damager.location.y && damager.location.y < 78.0){
                e.damage = 10.0
                return
            }else{
                e.damage = 20.0
                return
            }

        }
    }

    @EventHandler
    fun death(e: PlayerDeathEvent){
        if (!aoonistart)return

        if (scoreboard.getTeam("hiroshi")?.hasEntry(e.entity.name)!!){


            if (easy && !death || mode[e.entity] == "easy" && !death){
                val ki = zanki[e.entity.player!!]

                if (ki == 0){
                    for (p in Bukkit.getOnlinePlayers()){
                        p.sendMessage(ChatColor.BLUE.toString() + e.entity.player?.name + "が青鬼に食べられた")
                    }
                    die = true
                }else{
                    zanki[e.entity] = ki?.minus(1)!!
                    for (p in Bukkit.getOnlinePlayers()){
                        p.sendMessage(ChatColor.BLUE.toString() + e.entity.player?.name + "が青鬼に食べられた")
                    }
                    e.drops.clear()
                    e.entity.spigot().respawn()
                    return
                }
            }
            if (normal && !death){
                for (p in Bukkit.getOnlinePlayers()){
                    p.sendMessage(ChatColor.BLUE.toString() + e.entity.player?.name + "が青鬼に食べられた")
                }
                die = true
            }



            if (death){
                death = false
            }

            e.drops.clear()
            e.entity.spigot().respawn()
        }
        if (scoreboard.getTeam("hukkatu")?.hasEntry(e.entity.name)!!){
            e.drops.clear()
            e.entity.spigot().respawn()
        }


    }


    @EventHandler
    fun itemcatch(e: PlayerPickupItemEvent){
        if (!aoonistart)return
        if (scoreboard.getTeam("aooni")?.hasEntry(e.player.name)!!){
            e.isCancelled = true
        }

    }

    @EventHandler
    fun playerdrop(e: PlayerDropItemEvent){
        if (!aoonistart)return
        if (easy || normal){
            if (e.itemDrop.itemStack.type != Material.COOKED_PORKCHOP)e.isCancelled = true
            if (!e.player.isOnGround)e.isCancelled = true
            if (e.player.location.subtract(0.0,1.0,0.0).block.type == Material.PODZOL)e.isCancelled = true
        }
    }

    @EventHandler
    fun playerrespawn(e: PlayerRespawnEvent){
        if (!aoonistart)return
        if (die){
            die = false
            scoreboard.getTeam("hukkatu")?.addEntry(e.player.name)
            e.player.sendMessage("§e死んでしまってもアスレで復活しよう！")
            e.player.sendMessage("§aeasyは比較的簡単？なアスレです")
            e.player.sendMessage("§cnormalは比較的難しいですが、矢を牛に当てると復活できます")
            e.player.sendMessage("§6§l復活して勝利をつかみ取ろう！")
        }

        if (scoreboard.getTeam("hiroshi")?.hasEntry(e.player.name)!!){

            when(Random.nextInt(1..4)){
                1 -> {
                    e.respawnLocation = Location(e.player.world, -45.5, 51.0, 67.5)
                }

                2 -> {
                    e.respawnLocation = Location(e.player.world, 62.5, 51.0, 79.5)
                }
                3 -> {
                    e.respawnLocation = Location(e.player.world, -50.5, 63.0, 47.5)
                }
                4 -> {
                    e.respawnLocation = Location(e.player.world, -10.5, 51.0, 92.5)
                }


            }

        }
        if (scoreboard.getTeam("hukkatu")?.hasEntry(e.player.name)!!){
            bow[e.player] = true
            e.respawnLocation = Location(e.player.world, -31.0, 50.0, 4.0)
        }
        if (scoreboard.getTeam("hiroshi")?.size == 0){
            time = 0
        }


    }

    @EventHandler
    fun blockinteract(e: BlockPlaceEvent){

        if (!aoonistart)return
        if (e.block.type == Material.OAK_PRESSURE_PLATE && e.block.location == Location(e.player.world, 44.0, 51.0, 43.0)){
            for (p in Bukkit.getOnlinePlayers()){
                p.sendMessage(prefix + "§d${e.player.name} さんが地下室を開放しました")
                p.sendMessage("§d地下室が解放された！")
                p.sendMessage("§d地下室から羊毛を探し出して5階手前に置こう！")
            }
        }else{
            if (e.block.type == Material.BLUE_WOOL && e.block.location == Location(e.player.world, -1.0, 69.0, 39.0)){
                for (p in Bukkit.getOnlinePlayers()){
                    p.sendMessage(prefix + "§d${e.player.name} さんが5階へ行けるようにしました")
                    p.sendMessage("§b5階が解放された！")
                    p.sendMessage("§b感圧版に動力をいきわたらせて扉を開こう！(肉で代用可)")
                    p.sendMessage("§6§lカギをゲットしたら1階の玄関から脱出だ！")
                    p.sendMessage("§c§l注意：玄関から出るときは氷、脱出するときはダイヤモンドブロックをクリックします")
                }
            }else e.isCancelled = true
        }
    }



    @EventHandler
    fun move(e: PlayerMoveEvent){
        if (umastart){
            if (e.player.location.subtract(0.0,1.0,0.0).block.type == Material.COAL_BLOCK && scoreboard.getTeam("uma")?.hasEntry(e.player.name)!!){
                e.player.teleport(e.player.location.add(0.0,0.0,-5.0))
                loop[e.player] = loop[e.player]?.plus(1)!!
                if (loop[e.player] == kaisuu){
                    if (e.player.vehicle != null) e.player.vehicle!!.remove()
                    e.player.gameMode = GameMode.SPECTATOR
                    Bukkit.broadcastMessage(prefix + "${e.player.name}がゴールしました！(time:${firstumatime - umatime}秒)")

                    goallist.add(e.player)
                    scoreboard.getTeam("umagoal")?.addEntry(e.player.name)
                }else{
                    Bukkit.broadcastMessage(prefix + "${e.player.name}が${loop[e.player]}周目を通過しました！(time:${firstumatime - umatime}秒)")
                }
            }
        }
        if (e.player.location.subtract(0.0, 1.0, 0.0).block.type == Material.PODZOL && scoreboard.getTeam("aooni")?.hasEntry(e.player.name)!!){
            e.player.walkSpeed = 0.4f
        }else{
            e.player.walkSpeed = 0.2f
        }
        if (!aoonistart)return
        if (!e.player.isSneaking && !scoreboard.getTeam("hukkatu")?.hasEntry(e.player.name)!!){
            Bukkit.dispatchCommand(e.player, "sneak")
        }

    }


    @EventHandler
    fun sneak(e: PlayerToggleSneakEvent){
        if (umastart)e.isCancelled = true
        if (!aoonistart)return
        e.isCancelled = true
    }

    @EventHandler
    fun block(e: BlockBreakEvent){
        if (scoreboard.getTeam("hukkatu")?.hasEntry(e.player.name)!!)e.isCancelled = true
        if (scoreboard.getTeam("aooni")?.hasEntry(e.player.name)!!)e.isCancelled = true
        if (scoreboard.getTeam("hiroshi")?.hasEntry(e.player.name)!!)e.isCancelled = true

    }

    @EventHandler
    fun hugrey(e: FoodLevelChangeEvent){
        if (scoreboard.getTeam("aooni")?.hasEntry(e.entity.name)!!)e.isCancelled = true
        if (scoreboard.getTeam("hukkatu")?.hasEntry(e.entity.name)!!)e.isCancelled = true

    }

    @EventHandler
    fun entityinteract(e: PlayerInteractEntityEvent){
        if (!aoonistart)return
        if (e.rightClicked.type == EntityType.ITEM_FRAME){
            val clicked = e.rightClicked
            val frame = clicked as ItemFrame
            val item = frame.item
            when(item.type){
                Material.BOW -> {

                    if (scoreboard.getTeam("hukkatu")?.hasEntry(e.player.name)!! && bow[e.player] == true) {
                        bow[e.player] = false
                        e.player.inventory.clear()
                        e.player.inventory.setItemInMainHand(ItemStack(Material.BOW))
                        e.player.inventory.setItemInOffHand(ItemStack(Material.ARROW, 2))
                    }

                }

                Material.STONE_SWORD -> {
                    if (scoreboard.getTeam("hukkatu")?.hasEntry(e.player.name)!!) {
                        e.player.inventory.clear()
                        e.player.inventory.setItemInMainHand(ItemStack(Material.STONE_SWORD))
                    }
                }
                else -> return
            }
            e.isCancelled = true
        }
    }

    @EventHandler
    fun destroy(e: VehicleDestroyEvent){
        if (!aoonistart)return
        if (e.vehicle.type == EntityType.MINECART){
            e.attacker?.teleport(Location(e.attacker?.world, -128.0, 52.0, -6.0))
            e.isCancelled = true
        }

    }

    @EventHandler
    fun alldamage(e: EntityDamageEvent){

        if (e.entity.type == EntityType.PLAYER) {
            val p = e.entity as Player
            if (scoreboard.getTeam("aooni")?.hasEntry(p.name)!!){
                e.isCancelled = true
            }
        }
    }



}