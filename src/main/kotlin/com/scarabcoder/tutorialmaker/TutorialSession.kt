package com.scarabcoder.tutorialmaker

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

class TutorialSession(val player: Player, val tutorial: Tutorial) {

    private val delay: Long = 0
    private val period: Long = 0

    var page: Tutorial.Page = tutorial.pages[0]
    private var currentScrollingTask: ScrollingRunnable
    private val startLocation = player.location
    val pageIndex
        get() = tutorial.pages.indexOf(page)

    init {
        currentScrollingTask = ScrollingRunnable(page,if(page.location == null) player.location else page.location!!, player)
        currentScrollingTask.runTaskTimer(TutorialMaker.getPlugin(), delay, period)
    }

    fun nextPage(){
        currentScrollingTask.cancel()
        if(pageIndex + 1 == tutorial.pages.size){
            TutorialHandler.endTutorial(player)
        }else{
            page = tutorial.pages[pageIndex + 1]
            currentScrollingTask = ScrollingRunnable(page, if(page.location == null) player.location else page.location!!, player)
            currentScrollingTask.runTaskTimer(TutorialMaker.getPlugin(), delay, period)
        }
    }

    fun prevPage(){
        if(pageIndex == 0) return
        currentScrollingTask.cancel()
        page = tutorial.pages[pageIndex - 1]
        currentScrollingTask = ScrollingRunnable(page, if(page.location == null) player.location else page.location!!, player)
        currentScrollingTask.runTaskTimer(TutorialMaker.getPlugin(), delay, period)
    }

    fun end(){
        if(!currentScrollingTask.isCancelled)
            currentScrollingTask.cancel()
        player.teleport(startLocation)
    }

}