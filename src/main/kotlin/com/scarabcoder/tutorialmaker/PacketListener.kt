package com.scarabcoder.tutorialmaker

import io.netty.channel.ChannelDuplexHandler
import io.netty.channel.ChannelHandlerContext
import net.minecraft.server.v1_12_R1.PacketPlayInSteerVehicle
import org.bukkit.entity.Player

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
class PacketListener(val player: Player): ChannelDuplexHandler() {

    private var lastMove: Long = 0

    override fun channelRead(ctx: ChannelHandlerContext?, msg: Any?) {
        ctx!!
        msg!!

        if(msg::class.java.simpleName != "PacketPlayInSteerVehicle" || !TutorialHandler.isInTutorial(player) || System.currentTimeMillis() - lastMove < 150){ super.channelRead(ctx, msg); return }
        msg as PacketPlayInSteerVehicle
        val session = TutorialHandler.getSession(player)!!
        if(msg.a() != 0.toFloat()) {
            if (msg.a() < 0) session.nextPage() else session.prevPage()
            lastMove = System.currentTimeMillis()
        }
        super.channelRead(ctx, msg)
    }

}