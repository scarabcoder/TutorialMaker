package com.scarabcoder.tutorialmaker

import net.minecraft.server.v1_12_R1.*
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

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
class ScrollingRunnable(private val page: Tutorial.Page, val location: Location, val player: Player): BukkitRunnable() {

    private val index: Int = 0
    private val line = 1


    private val lineEntities: MutableList<EntityArmorStand> = ArrayList()

    init {

        val entity = EntityArmorStand((location.world as CraftWorld).handle)
        entity.setPosition(location.x, location.y + 3, location.z)
        entity.customNameVisible = true
        entity.customName = page.title
        entity.isInvisible = true
        entity.isMarker = true


        val spawnTitle = PacketPlayOutSpawnEntityLiving(entity)

        ((player as CraftPlayer).handle).playerConnection.sendPacket(spawnTitle)

    }

    override fun run() {
        if(lineEntities.size < line){
            val entity = EntityArmorStand((location.world as CraftWorld).handle)
            entity.customNameVisible = true
            entity.customName = ""
            entity.isInvisible = true
            entity.isMarker = true
            entity.setPosition(location.x, location.y * (lineEntities.size.toDouble() * 0.35), location.z)
            lineEntities.add(entity)
            ((player as CraftPlayer).handle).playerConnection.sendPacket(PacketPlayOutSpawnEntityLiving(entity))
        }

        val entity = lineEntities[line - 1]

        val dataWatcher = entity.dataWatcher
        val tag = entity.save(NBTTagCompound())
        tag.setString("CustomName", tag.getString("CustomName") + "1")
        dataWatcher.register(DataWatcherObject<NBTTagCompound>(13, DataWatcherRegistry.n), tag)
        dataWatcher.javaClass.getMethod("")
        val packet = PacketPlayOutEntityMetadata(entity.id, dataWatcher, true)

        ((player as CraftPlayer).handle).playerConnection.sendPacket(packet)

    }

}