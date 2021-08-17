package gameplugin.gameplugin

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scoreboard.Team

var prefix = "§a[GamePlugin]§f "


var taizai = true
var time = 900
var onitime = 180
val sbm = Bukkit.getScoreboardManager()
val scoreboard = sbm.mainScoreboard
lateinit var plugin : Main

var aoonistart = false
var umastart = false
val loop = hashMapOf<Player,Int>()
var kaisuu = 0
val goallist = arrayListOf<Player>()
var firstumatime = 0
var easy = false
var normal = true
var zanki = HashMap<Player,Int>()
var die = false


class Main : JavaPlugin(){

    companion object{
        val huwadamamenu = Bukkit.createInventory(null,9,"フワ玉")
    }
    override fun onEnable() {
        plugin = this
        server.pluginManager.registerEvents(EventListener, plugin)
        aoonistart = false
        val washitu = ItemStack(Material.PAPER)
        val piano = ItemStack(Material.PAPER)
        val washitumeta = washitu.itemMeta
        washitumeta.setDisplayName("和室")
        val pianometa = piano.itemMeta
        pianometa.setDisplayName("ピアノ部屋")
        washitu.itemMeta = washitumeta
        piano.itemMeta = pianometa
        huwadamamenu.setItem(2,washitu)
        huwadamamenu.setItem(6,piano)
        getCommand("gp")?.setExecutor(GameCommand)
        getCommand("return")?.setExecutor(GameCommand)
        server.logger.info("enable")


        var aooni = scoreboard.getTeam("aooni")
        if (aooni == null) aooni = scoreboard.registerNewTeam("aooni")

        aooni.prefix = ChatColor.BLUE.toString() + "[青鬼]"

        var hiroshi = scoreboard.getTeam("hiroshi")
        if (hiroshi == null) hiroshi = scoreboard.registerNewTeam("hiroshi")

        hiroshi.setAllowFriendlyFire(false)

        hiroshi.prefix = "[ひろし]"

        var hukkatu = scoreboard.getTeam("hukkatu")
        if (hukkatu == null) hukkatu = scoreboard.registerNewTeam("hukkatu")

        hukkatu.setAllowFriendlyFire(false)

        var spectator = scoreboard.getTeam("spectator")
        if (spectator == null) spectator = scoreboard.registerNewTeam("spectator")

        spectator.prefix = "§a[観戦]"

        var goal = scoreboard.getTeam("goal")
        if (goal == null) goal = scoreboard.registerNewTeam("goal")

        goal.prefix = "§b[クリア者]"
    }

    override fun onDisable() {

    }



}