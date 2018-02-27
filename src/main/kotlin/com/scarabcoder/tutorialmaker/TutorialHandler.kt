package com.scarabcoder.tutorialmaker

import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/*
 * The MIT License
 *
 * Copyright 2018 Nicholas Harris
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
object TutorialHandler : Listener {

    private val tutorials: HashMap<String, Tutorial> = HashMap()
    private val sessions: HashMap<UUID, TutorialSession> = HashMap()

    init {
        val cfgSection = TutorialMaker.getPlugin().config.getConfigurationSection("tutorials")
        for(key in cfgSection.getKeys(false)){
            val tutorialSection = cfgSection.getConfigurationSection(key)
            val pages: MutableList<Tutorial.Page> = ArrayList()
            for(page in tutorialSection.getKeys(false)){
                val loc = tutorialSection.get(page + ".location") as Location
                val dir = tutorialSection.getVector(page + ".direction")
                pages.add(Tutorial.Page(ChatColor.translateAlternateColorCodes('&', tutorialSection.getString(page + ".title")),
                        ChatColor.translateAlternateColorCodes('&', tutorialSection.getString(page + ".text")), loc, dir))
            }
            tutorials.put(key, Tutorial(key, pages))
        }
    }

    fun saveTutorial(tutorial: Tutorial){
        val cfg = TutorialMaker.getPlugin().config
        val cfgSection = cfg.createSection("tutorials." + tutorial.name)
        for((x, page) in tutorial.pages.withIndex()) {
            val pageSection = cfgSection.createSection("page$x")
            pageSection.set("text", page.text)
            pageSection.set("title", page.title)
            pageSection.set("direction", page.direction)
            pageSection.set("location", page.location)
        }
        TutorialMaker.getPlugin().saveConfig()
    }

    fun registerTutorial(tutorial: Tutorial) {
        tutorials.put(tutorial.name, tutorial)
    }

    fun isInTutorial(player: Player): Boolean {
        return sessions.containsKey(player.uniqueId)
    }

    fun startTutorial(player: Player, tutorial: Tutorial){
        sessions.put(player.uniqueId, TutorialSession(player, tutorial))
    }

    fun getTutorial(name: String): Tutorial? {
        return tutorials[name]
    }

    fun getSession(player: Player): TutorialSession? {
        return sessions[player.uniqueId]
    }

    fun endTutorial(player: Player) {
        sessions.remove(player.uniqueId)
    }

    @EventHandler
    private fun registerPacketListener(e: PlayerJoinEvent){
        val channel = (e.player as CraftPlayer).handle.playerConnection.networkManager.channel
        channel.pipeline().addBefore("packet_handler", "PacketInjector", PacketListener(e.player))
    }

    @EventHandler
    private fun onPlayerQuit(e: PlayerQuitEvent){
        endTutorial(e.player)
    }

}