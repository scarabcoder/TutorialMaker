package com.scarabcoder.tutorialmaker

import net.novapixelnetwork.commandapi.Command
import net.novapixelnetwork.commandapi.CommandSection
import org.bukkit.ChatColor
import org.bukkit.entity.Player

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
class TutorialCommand: CommandSection("tutorial") {

    override fun onCommand(player: Player) {

    }

    @Command
    fun create(sender: Player, name: String){
        if(EditorManager.isEditing(sender)){
            sender.sendMessage("${ChatColor.RED}You must save the current tutorial before creating a new one!")
            return
        }
        if(TutorialHandler.getTutorial(name) != null){
            sender.sendMessage("${ChatColor.RED}Tutorial with that name already exists!")
            return
        }

        EditorManager.newEditSession(name, sender)
        sender.sendMessage("${ChatColor.GREEN}Created tutorial $name. Create pages with /newpage <title>, save with /tutorial save.")

    }

    @Command
    fun save(sender: Player){
        if(!EditorManager.isEditing(sender)){
            sender.sendMessage("${ChatColor.RED}You are not editing a tutorial!")
            return
        }
        EditorManager.endEditSession(sender)
        sender.sendMessage("${ChatColor.GREEN}Saved & registered tutorial.")
    }

    @Command
    fun start(sender: Player, name: String){
        if(TutorialHandler.isInTutorial(sender)){
            sender.sendMessage("${ChatColor.RED}Please exit your current tutorial before starting a new one.")
            return
        }
        val tut = TutorialHandler.getTutorial(name)
        if(tut == null){
            sender.sendMessage("${ChatColor.RED}Tutorial $name not found!")
            return
        }

        TutorialHandler.startTutorial(sender, tut)

    }


}