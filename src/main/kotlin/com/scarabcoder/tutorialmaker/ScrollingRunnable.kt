package com.scarabcoder.tutorialmaker

import net.minecraft.server.v1_12_R1.*
import org.apache.commons.lang3.text.WordUtils
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

    private var index = 0
    private var line = 1
    private val lines = WordUtils.wrap(page.text, 50, "\n", true).split("\n")
    private val title: EntityArmorStand
    private val camera: EntityArmorStand

    private val lineEntities: MutableList<EntityArmorStand> = ArrayList()

    init {

        val entity = EntityArmorStand((location.world as CraftWorld).handle)
        entity.setPosition(location.x, location.y + 0.5, location.z)
        entity.customNameVisible = true
        entity.customName = page.title
        entity.isInvisible = true
        entity.isMarker = true


        val spawnTitle = PacketPlayOutSpawnEntityLiving(entity)

        (player as CraftPlayer).handle.playerConnection.sendPacket(spawnTitle)
        title = entity

        camera = EntityArmorStand((location.world as CraftWorld).handle)
        camera.setPosition(location.x, location.y, location.z)
        camera.isInvisible = true
        camera.isMarker = true
        camera.isNoGravity = true

        player.handle.playerConnection.sendPacket(PacketPlayOutSpawnEntityLiving(camera))
        player.handle.playerConnection.sendPacket(PacketPlayOutGameStateChange(3, 3.toFloat()))
        camera.passengers.add(player.handle)
        player.handle.playerConnection.sendPacket(PacketPlayOutMount(camera))
        //player.handle.playerConnection.sendPacket(PacketPlayOutCamera(camera))

    }

    override fun run() {
        if(line > lines.size) return
        if(lineEntities.size < line){
            val entity = EntityArmorStand((location.world as CraftWorld).handle)
            entity.customNameVisible = true
            entity.customName = ""
            entity.isInvisible = true
            entity.isMarker = true
            entity.isNoGravity = true
            entity.setPosition(location.x, location.y - (line * 0.25), location.z)
            lineEntities.add(entity)
            ((player as CraftPlayer).handle).playerConnection.sendPacket(PacketPlayOutSpawnEntityLiving(entity))
        }

        val entity = lineEntities[line - 1]
        entity.customName += lines[line - 1][index]

        val dataWatcher = entity.dataWatcher
        val tag = entity.save(NBTTagCompound())
        tag.setString("CustomName", tag.getString("CustomName") + "1")
        val dataWatcherObj = DataWatcherObject<String>(2, DataWatcherRegistry.d)
        val item: DataWatcher.Item<String> = DataWatcher.Item(dataWatcherObj, entity.customName)
        val packet = PacketPlayOutEntityMetadata(entity.id, dataWatcher, true)

        val metaData = packet::class.java.getDeclaredField("b")
        metaData.isAccessible = true
        val metaDataVal: MutableList<DataWatcher.Item<*>> = metaData.get(packet) as MutableList<DataWatcher.Item<*>>
        metaDataVal.add(item)
        metaData.set(packet, metaDataVal)


        ((player as CraftPlayer).handle).playerConnection.sendPacket(packet)

        index++
        if(index > lines[line - 1].length - 1){
            index = 0
            line++
        }

    }

    override fun cancel() {
        val craftPlayer = (player as CraftPlayer).handle
        craftPlayer.playerConnection.sendPacket(PacketPlayOutEntityDestroy(title.id))
        craftPlayer.playerConnection.sendPacket(PacketPlayOutEntityDestroy(camera.id))
        lineEntities.forEach { craftPlayer.playerConnection.sendPacket(PacketPlayOutEntityDestroy(it.id)) }
        super.cancel()
    }

}