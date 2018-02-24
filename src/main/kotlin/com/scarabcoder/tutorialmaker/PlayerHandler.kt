package com.scarabcoder.tutorialmaker

import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/*
 * The MIT License
 *
 * Copyright 2018 Nova Pixel Network
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
object PlayerHandler {

    private val tutorials: HashMap<String, Tutorial> = HashMap()

    private val playersInTutorial: HashMap<UUID, String> = HashMap()
    private val previousLocations: HashMap<UUID, Location> = HashMap()

    init {
        val cfgSection = TutorialMaker.getPlugin().config.getConfigurationSection("tutorials")
        for(key in cfgSection.getKeys(false)){
            val tutorialSection = cfgSection.getConfigurationSection(key)
            val pages: MutableList<Tutorial.Page> = ArrayList()
            for(page in tutorialSection.getKeys(false)){
                pages.add(Tutorial.Page(ChatColor.translateAlternateColorCodes('&', tutorialSection.getString(page + ".title")),
                        ChatColor.translateAlternateColorCodes('&', tutorialSection.getString(page + ".text"))))
            }
            tutorials.put(key, Tutorial(key, pages))
        }
    }

    fun isInTutorial(player: Player): Boolean {
        return playersInTutorial.containsKey(player.uniqueId)
    }

    fun startTutorial(player: Player, tutorial: Tutorial){
        playersInTutorial.put(player.uniqueId, tutorial.name)
        previousLocations.put(player.uniqueId, player.location)
    }

    fun getTutorial(name: String): Tutorial? {
        return tutorials[name]
    }

    fun page(player: Player, page: Tutorial.Page){

    }

    fun endTutorial(player: Player) {
        playersInTutorial.remove(player.uniqueId)
    }

    @EventHandler
    private fun onPlayerQuit(e: PlayerQuitEvent){
        endTutorial(e.player)
    }

}