package com.scarabcoder.tutorialmaker

import org.bukkit.entity.Player
import java.util.*

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
object EditorManager {

    private val editors: HashMap<UUID, Tutorial> = HashMap()

    fun isEditing(player: Player): Boolean = editors.containsKey(player.uniqueId)

    fun newEditSession(name: String, player: Player) = editors.put(player.uniqueId, Tutorial(name, ArrayList()))

    fun getEditing(player: Player): Tutorial? = editors[player.uniqueId]

    fun endEditSession(player: Player) {
        if(!isEditing(player)) return
        TutorialHandler.saveTutorial(editors[player.uniqueId]!!)
        editors.remove(player.uniqueId)
    }


}