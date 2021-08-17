    package gameplugin.gameplugin

    import gameplugin.gameplugin.EventListener.cooltime
    import gameplugin.gameplugin.EventListener.huwadamause
    import gameplugin.gameplugin.Util.aooniselect
    import org.bukkit.*
    import org.bukkit.command.Command
    import org.bukkit.command.CommandExecutor
    import org.bukkit.command.CommandSender
    import org.bukkit.entity.EntityType
    import org.bukkit.entity.Player
    import org.bukkit.inventory.ItemStack
    import org.bukkit.potion.PotionEffect
    import org.bukkit.potion.PotionEffectType
    import org.bukkit.scheduler.BukkitRunnable
    import org.bukkit.scoreboard.DisplaySlot
    import org.bukkit.scoreboard.Scoreboard
    import org.bukkit.scoreboard.ScoreboardManager
    import java.util.*
    import kotlin.collections.HashMap


    object GameCommand : CommandExecutor {

        lateinit var scoreboardManager: ScoreboardManager
        var hiroshisc = HashMap<OfflinePlayer,Scoreboard>()
        var aoonisc = HashMap<OfflinePlayer, Scoreboard>()
        var spesc = HashMap<OfflinePlayer, Scoreboard>()
        var mode = HashMap<OfflinePlayer,String>()
        var umatime = 0

        override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

            if (sender !is Player) return false
            if (command.label == "return"){
                sender.teleport(Location(sender.world,-95.5,34.0,-155.5))
                sender.sendMessage("$prefix§aロビーに転送しました")
                return true
            }

            when (args[0]) {
                "help" -> {
                    sender.sendMessage("§a=====================GamePlugin=====================")
                    sender.sendMessage("§9/aooni help §f青鬼のヘルプを表示できます")
                    sender.sendMessage("§a=====================GamePlugin===Author:tororo_1066")

                }
                "cm"->{
                    if (args.size != 2)return true
                    try {
                        val item = sender.inventory.itemInMainHand
                        val meta = item.itemMeta
                        meta.setCustomModelData(args[1].toInt())
                        item.itemMeta = meta
                    }catch (e : NumberFormatException){
                        return true
                    }

                }

                "uma"-> {
                    if (!sender.hasPermission("admin"))return true
                    when(args[1]){
                        "join"->{
                            for (player in Bukkit.getOnlinePlayers()) {
                                if (player.location.subtract(0.0, 1.0, 0.0).block.type == Material.EMERALD_BLOCK && player.location.subtract(0.0, 2.0, 0.0).block.type == Material.EMERALD_BLOCK){
                                    scoreboard.getTeam("uma")?.addEntry(player.name)
                                    Bukkit.broadcastMessage(prefix + "${player.name}が参加しました")
                                }


                            }
                        }
                        "end"->{
                            if (!umastart)return true
                            umatime = 0
                        }
                        "start"->{
                            if (args.size != 4)return true
                            try {
                                if (args[2].toInt() !in 1..10)return true
                                if (args[3].toInt() < 0)return true
                            }catch (e : NumberFormatException){
                                return true
                            }
                            if (umastart)return true
                            for (p in Bukkit.getOnlinePlayers()){
                                if (scoreboard.getTeam("uma")?.hasEntry(p.name)!!){
                                    if (p.vehicle == null || p.vehicle?.type != EntityType.HORSE){
                                        if (p.vehicle?.type != EntityType.PIG){
                                            Bukkit.broadcastMessage("$prefix$p　さんが馬に乗っていません")
                                            return true
                                        }

                                    }
                                }
                            }
                            umastart = true
                            loop.clear()
                            var umast = 3
                            umatime = args[3].toInt()+3
                            firstumatime = args[3].toInt()
                            kaisuu = args[2].toInt() + 1

                            object : BukkitRunnable(){
                                override fun run() {
                                    scoreboardManager = Bukkit.getScoreboardManager()
                                    if (umast in 0..3){
                                        if (umast == 0){
                                            for (p in Bukkit.getOnlinePlayers()){
                                                p.sendTitle("§e§lーーーSTART!ーーー","",0,60,10)
                                                p.world.playSound(p.location, Sound.ENTITY_GENERIC_EXPLODE, 100f, 1f)
                                                if (scoreboard.getTeam("uma")?.hasEntry(p.name) == true){
                                                    p.addPotionEffect(PotionEffect(PotionEffectType.GLOWING,100000,1))
                                                    if (p.vehicle?.type == EntityType.HORSE){
                                                        p.teleport(Location(p.world, 155.0,6.0,265.0,180f,0f))
                                                    }else{
                                                        p.teleport(Location(p.world,124.0,6.0,260.0,180f,0f))
                                                    }

                                                    loop[p] = 0
                                                }

                                            }
                                        }else{
                                            for (p in Bukkit.getOnlinePlayers()){
                                                p.sendTitle("§aーーー${umast}ーーー","",0,20,0)
                                                p.world.playSound(p.location, Sound.BLOCK_ANVIL_PLACE, 100f, 1f)
                                            }
                                        }

                                        umast--
                                    }
                                    if (umatime <= 0){
                                        for (p in Bukkit.getOnlinePlayers()){
                                            p.sendTitle("§l終了","", 0, 60, 10)
                                            p.activePotionEffects.clear()
                                            p.sendMessage("§aゴール者(ゴール順)")
                                            for (pp in goallist){
                                                p.sendMessage("§e" + pp.name)
                                            }
                                            scoreboard.getTeam("uma")?.removeEntry(p.name)
                                            scoreboard.getTeam("umagoal")?.removeEntry(p.name)
                                        }
                                        
                                        goallist.clear()
                                        umastart = false
                                        cancel()
                                    }
                                    if (scoreboard.getTeam("uma")?.size == 0)umatime = 0
                                    for (p in Bukkit.getOnlinePlayers()){
                                        val score = scoreboardManager.newScoreboard
                                        val ob = score.registerNewObjective("Dummy","Dummy",p.name)
                                        ob.displaySlot = DisplaySlot.SIDEBAR
                                        ob.getScore("§a周回数:${loop[p]}/$kaisuu").score = 0
                                        ob.getScore("§d残り時間:$umatime").score = -1
                                        var c = -2
                                        for (pp in scoreboard.getTeam("uma")?.players!!){
                                            ob.getScore("§b${pp.player?.name}:${loop[pp.player]}/$kaisuu").score = c
                                            c--
                                        }
                                        p.scoreboard = score
                                    }

                                    umatime--
                                }
                            }.runTaskTimer(plugin,0,20)

                        }
                    }
                }
                "aooni" -> {
                    when (args[1]) {

                        "help" -> {
                            sender.sendMessage("§9=====================AooniGokko=====================")
                            sender.sendMessage("§9/return §dロビーにTPします")
                            sender.sendMessage("§9/start §d青鬼ごっこをスタートします")
                            sender.sendMessage("§9/wp (Player) §d青鬼を選択します(wpだけだと抽選(ダイブロ2段の上))")
                            sender.sendMessage("§9/stop §d青鬼ごっこを終了します")
                            sender.sendMessage("§9/time (Time) §d青鬼ごっこの時間を変更します(プレイ中のみ)")
                            sender.sendMessage("§9/mode (easy or normal) (Player) §d難易度を変更します")
                            sender.sendMessage("§9/spe §d観戦モードになります(wpの後に使用してください)")
                            sender.sendMessage("§9=====================AooniGokko=====================")
                        }
                        "start" -> {

                            if (aoonistart)return false
                            for (p in Bukkit.getOnlinePlayers()){
                                Bukkit.dispatchCommand(p,"sneak")
                            }
                            aoonistart = true


                            object : BukkitRunnable() {
                                var start = 3
                                override fun run() {

                                    if (start == 3) {
                                        for (p in Bukkit.getOnlinePlayers()) {
                                            p.world.playSound(p.location, Sound.BLOCK_ANVIL_PLACE, 100f, 1f)
                                            p.sendTitle("§9ーーー3ーーー", "", 0, 20, 0)
                                        }
                                    }
                                    if (start == 2) {
                                        for (p in Bukkit.getOnlinePlayers()) {
                                            p.world.playSound(p.location, Sound.BLOCK_ANVIL_PLACE, 100f, 1f)
                                            p.sendTitle("§9ーーー2ーーー", "", 0, 20, 0)
                                        }
                                    }
                                    if (start == 1) {

                                        for (p in Bukkit.getOnlinePlayers()) {
                                            p.world.playSound(p.location, Sound.BLOCK_ANVIL_PLACE, 100f, 1f)
                                            p.sendTitle("§9ーーー1ーーー", "", 0, 20, 0)
                                        }
                                    }
                                    if (start == 0) {

                                        for (p in Bukkit.getOnlinePlayers()) {
                                            if (scoreboard.getTeam("spectator")?.hasEntry(p.name)!!){
                                                p.gameMode = GameMode.SPECTATOR
                                            }
                                            if (scoreboard.getTeam("hiroshi")?.hasEntry(p.name)!!){
                                                p.foodLevel = 20
                                                p.inventory.clear()
                                                zanki[p.player!!] = 2
                                                p.teleport(Location(Bukkit.getWorld("world"), 4.0, 50.0, 16.0))
                                            }
                                            p.world.playSound(p.location, Sound.ENTITY_GENERIC_EXPLODE, 100f, 1f)
                                            p.sendTitle("§9青§f鬼ごっこ", "START", 1, 100, 20)
                                            p.sendMessage("§9青§f鬼の館から脱出しよう！")
                                            p.sendMessage("1.§eまずは1~4階のチェストから感圧版を見つけよう！")
                                            p.sendMessage("§eそれを地下室の入り口に置くと地下への道が開かれる...！")
                                            p.sendMessage("2.§d地下のチェストから青の羊毛を探そう！")
                                            p.sendMessage("§dそれを5階の階段前に置くと道が開かれる...！")
                                            p.sendMessage("3.§b5階の奥のダイヤモンドをとって玄関から出よう！")
                                            p.sendMessage("§b氷、ダイヤモンドブロックを右クリックだ！")
                                        }


                                    }
                                    if (start == -3){
                                        huwadamause = true
                                        val block = sender.world.getBlockAt(-10,51,75)
                                        block.breakNaturally(ItemStack(Material.OAK_PRESSURE_PLATE))
                                        cancel()
                                    }
                                    start--


                                }

                            }.runTaskTimer(plugin, 0, 20)
                            time = 900
                            onitime = 180

                            object : BukkitRunnable() {


                                override fun run() {

                                    if (time == 0) {
                                        huwadamause = false
                                        aoonistart = false
                                        val list = arrayListOf<String>()
                                        for (pl in scoreboard.getTeam("goal")?.players!!){
                                            pl.name?.let { list.add(it) }
                                        }
                                        for (p in Bukkit.getOnlinePlayers()){
                                            p.sendTitle("§9青§f鬼ごっこ", "終了", 1, 100, 20)
                                            p.sendMessage("§a脱出者一覧(脱出順)===============")
                                            for (s in list.reversed()){
                                                p.sendMessage("§e§l$s")
                                            }
                                            p.sendMessage("§a===================================")
                                            Bukkit.dispatchCommand(p,"sneak")
                                            p.foodLevel = 20
                                            scoreboard.getTeam("aooni")?.removeEntry(p.name)
                                            scoreboard.getTeam("hiroshi")?.removeEntry(p.name)
                                            scoreboard.getTeam("hukkatu")?.removeEntry(p.name)
                                            scoreboard.getTeam("spectator")?.removeEntry(p.name)
                                            scoreboard.getTeam("goal")?.removeEntry(p.name)
                                        }
                                        cancel()
                                    }


                                    scoreboardManager = Bukkit.getScoreboardManager()
                                    for (a in scoreboard.getTeam("aooni")?.players!!){
                                        taizai = onitime != 0
                                        if (74.0 < a.player?.location?.y!! && a.player?.location?.y!! < 78.0 && taizai){
                                            onitime--
                                        }

                                        aoonisc[a] = scoreboardManager.newScoreboard
                                        val ob = aoonisc[a]?.registerNewObjective(a.name!!,"Dummy",a.name!!)
                                        ob?.displaySlot = DisplaySlot.SIDEBAR
                                        if (time > 450)ob?.getScore("§a残り時間:" + time + "秒")?.score = 0
                                        if (time in 61..449)ob?.getScore("§e残り時間:" + time + "秒")?.score = 0
                                        if (time in 0..60)ob?.getScore("§c残り時間:" + time + "秒")?.score = 0

                                        ob?.getScore("§35階滞在可能時間:" + onitime + "秒")?.score = -1
                                        if (!huwadamause) {
                                            ob?.getScore("§bフワ玉:" + cooltime + "秒")?.score = -2
                                        }else{
                                            aoonisc[a]?.resetScores("§bフワ玉:" + cooltime + "秒")
                                        }
                                        a.player?.scoreboard = aoonisc[a]!!
                                    }


                                    for (h in scoreboard.getTeam("hiroshi")?.players!!){
                                        hiroshisc[h] = scoreboardManager.newScoreboard
                                        val ob = hiroshisc[h]?.registerNewObjective(h.name!!,"Dummy",h.name!!)
                                        ob?.displaySlot = DisplaySlot.SIDEBAR
                                        if (time > 450)ob?.getScore("§a残り時間:" + time + "秒")?.score = 0
                                        if (time in 61..449)ob?.getScore("§e残り時間:" + time + "秒")?.score = 0
                                        if (time in 0..60)ob?.getScore("§c残り時間:" + time + "秒")?.score = 0
                                        ob?.getScore("§6残り人数:" + scoreboard.getTeam("hiroshi")?.size + "人")?.score = -1
                                        var count = 0
                                        for (hi in scoreboard.getTeam("hiroshi")?.players!!){
                                            if(mode[hi] == "easy"){
                                                count++
                                            }
                                        }
                                        if (easy && count == 0 || mode[h] == "easy"){
                                            ob?.getScore("§3残機:" + zanki[h])?.score = -2
                                        }

                                        h.player?.scoreboard = hiroshisc[h]!!
                                    }

                                    for (hu in scoreboard.getTeam("hukkatu")?.players!!){
                                        hiroshisc[hu] = scoreboardManager.newScoreboard
                                        val ob = hiroshisc[hu]?.registerNewObjective(hu.name!!,"Dummy",hu.name!!)
                                        ob?.displaySlot = DisplaySlot.SIDEBAR
                                        if (time > 450)ob?.getScore("§a残り時間:" + time + "秒")?.score = 0
                                        if (time in 61..449)ob?.getScore("§e残り時間:" + time + "秒")?.score = 0
                                        if (time in 0..60)ob?.getScore("§c残り時間:" + time + "秒")?.score = 0
                                        ob?.getScore("§6残り人数:" + scoreboard.getTeam("hiroshi")?.size + "人")?.score = -1

                                        hu.player?.scoreboard = hiroshisc[hu]!!
                                    }

                                    for (spe in scoreboard.getTeam("spectator")?.players!!){


                                        spesc[spe] = scoreboardManager.newScoreboard
                                        val ob = spesc[spe]?.registerNewObjective(spe.name!!,"Dummy",spe.name!!)!!

                                        ob.displaySlot = DisplaySlot.SIDEBAR
                                        if (time > 450) ob.getScore("§a残り時間:" + time + "秒").score = 0
                                        if (time in 61..449) ob.getScore("§e残り時間:" + time + "秒").score = 0
                                        if (time in 0..60) ob.getScore("§c残り時間:" + time + "秒").score = 0
                                        ob.getScore("§6残り人数:" + scoreboard.getTeam("hiroshi")?.size + "人").score = -1
                                        ob.getScore("§35階滞在可能時間:" + onitime + "秒").score = -2
                                        ob.getScore("§bフワ玉:" + cooltime + "秒").score = -3
                                        spe.player?.scoreboard = spesc[spe]!!
                                    }

                                    for (g in scoreboard.getTeam("goal")?.players!!){


                                        hiroshisc[g] = scoreboardManager.newScoreboard
                                        val ob = hiroshisc[g]?.registerNewObjective(g.name!!,"Dummy",g.name!!)
                                        ob?.displaySlot = DisplaySlot.SIDEBAR
                                        if (time > 450)ob?.getScore("§a残り時間:" + time + "秒")?.score = 0
                                        if (time in 61..449)ob?.getScore("§e残り時間:" + time + "秒")?.score = 0
                                        if (time in 0..60)ob?.getScore("§c残り時間:" + time + "秒")?.score = 0
                                        ob?.getScore("§6残り人数:" + scoreboard.getTeam("hiroshi")?.size + "人")?.score = -1
                                        g.player?.scoreboard = hiroshisc[g]!!
                                    }





                                    time--
                                }
                            }.runTaskTimer(plugin, 60, 20)
                        }

                        "wp" -> {

                            if (args.size == 3) {
                                val oni = Bukkit.getPlayer(args[2])
                                if (oni != null) {
                                    if (!oni.isOnline){
                                        sender.sendMessage(prefix + "このプレイヤーはこのサーバーにいません！")
                                        return true
                                    }
                                        for (p in Bukkit.getOnlinePlayers()) {
                                            p.addAttachment(plugin).setPermission("irondoors.use",false)
                                            p.gameMode = GameMode.SURVIVAL
                                            scoreboard.getTeam("hiroshi")?.addEntry(p.name)
                                            p.foodLevel = 20
                                        }
                                        aooniselect(oni)


                                    } else {
                                        sender.sendMessage(prefix + "このプレイヤーは存在しません！")
                                    return true
                                    }


                            } else {

                                val playerList = arrayListOf<Player>()

                                for (player in Bukkit.getOnlinePlayers()) {
                                    if (player.location.subtract(0.0, 1.0, 0.0).block.type == Material.DIAMOND_BLOCK && player.location.subtract(0.0, 2.0, 0.0).block.type == Material.DIAMOND_BLOCK) playerList.add(player)
                                }
                                if (playerList.isEmpty()) {
                                    sender.sendMessage(prefix + "誰か一人はダイヤモンドブロックの上に乗ってください！")
                                } else {
                                    val oni = playerList[Random().nextInt(playerList.size)]
                                    for (p in Bukkit.getOnlinePlayers()) {
                                        scoreboard.getTeam("hiroshi")?.addEntry(p.name)
                                        p.gameMode = GameMode.SURVIVAL
                                    }
                                    aooniselect(oni)
                                }
                            }
                        }

                        "time" -> {
                            try {
                                val number = args[2].toInt()
                                time = number
                            }catch (e : NumberFormatException){
                                sender.sendMessage("$prefix/gp aooni time (number)で使用できます")
                            }
                        }

                        "stop" -> {
                            time = 0
                        }

                        "mode" ->{
                            if (args.size == 3) {
                                if (aoonistart) {
                                    sender.sendMessage(prefix + "青鬼ごっこは始まっています！")
                                    return true
                                }
                                when (args[2]) {
                                    "easy" -> {
                                        easy = true
                                        normal = false
                                        for (p in Bukkit.getOnlinePlayers()) {
                                            p.sendMessage(prefix + "モードがeasyに変更されました")
                                        }
                                    }
                                    "normal" -> {
                                        normal = true
                                        easy = false
                                        for (p in Bukkit.getOnlinePlayers()) {
                                            p.sendMessage(prefix + "モードがnormalに変更されました")
                                        }

                                    }
                                }
                            }
                            if (args.size == 4){
                                if (aoonistart) {
                                    sender.sendMessage(prefix + "青鬼ごっこは始まっています！")
                                    return true
                                }
                                val player = Bukkit.getPlayer(args[3])
                                if (player == null){
                                    sender.sendMessage(prefix + "このプレイヤーは存在しません！")
                                    return true
                                }
                                if (!player.isOnline){
                                    sender.sendMessage(prefix + "このプレイヤーはこのサーバーにいません！")
                                    return true
                                }
                                if (!scoreboard.getTeam("hiroshi")?.hasEntry(player.name)!!){
                                    sender.sendMessage(prefix + "このプレイヤーはひろしではありません！")
                                    return true
                                }
                                when(args[2]){
                                    "easy"->{
                                        mode[player] = "easy"
                                        for (p in Bukkit.getOnlinePlayers()){
                                            p.sendMessage(prefix + "${player.name}のモードがeasyになりました")
                                        }
                                        return true
                                    }
                                    "normal"->{
                                        mode[player] = "normal"
                                        for (p in Bukkit.getOnlinePlayers()){
                                            p.sendMessage(prefix + "${player.name}のモードがnormalになりました")
                                        }
                                        return true
                                    }
                                }
                            }
                        }
                        "spe"->{
                            if (!sender.hasPermission("admin"))return true
                            if (args.size == 3){
                                val p = Bukkit.getPlayer(args[2])
                                if (p == null){
                                    sender.sendMessage(prefix + "プレイヤーを取得できませんでした")
                                    return true
                                }
                                if (!p.isOnline){
                                    p.sendMessage(prefix + "プレイヤーがオンラインではありません")
                                    return true
                                }
                                if (scoreboard.getTeam("spectator")?.hasEntry(p.name)!!){
                                    scoreboard.getTeam("hiroshi")?.addEntry(p.name)
                                    for (pl in Bukkit.getOnlinePlayers()){
                                        pl.sendMessage(prefix + "§a${p.name}が観戦をキャンセルしました")
                                    }
                                }else{
                                    scoreboard.getTeam("spectator")?.addEntry(p.name)
                                    for (pl in Bukkit.getOnlinePlayers()){
                                        pl.sendMessage(prefix + "§a${p.name}が観戦席に移動しました")
                                    }
                                }
                                return true
                            }
                            if (scoreboard.getTeam("spectator")?.hasEntry(sender.name)!!){
                                scoreboard.getTeam("hiroshi")?.addEntry(sender.name)
                                for (p in Bukkit.getOnlinePlayers()){
                                    p.sendMessage(prefix + "§a${sender.name}が観戦をキャンセルしました")
                                }
                            }else{
                                scoreboard.getTeam("spectator")?.addEntry(sender.name)
                                for (p in Bukkit.getOnlinePlayers()){
                                    p.sendMessage(prefix + "§a${sender.name}が観戦席に移動しました")
                                }
                            }
                            return true
                        }
                    }
                }
            }
            return true
        }


    }
