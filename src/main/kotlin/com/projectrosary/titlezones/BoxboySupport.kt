/*
 * MIT License
 *
 * Copyright (c) 2018 widd
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.projectrosary.titlezones

import com.github.xemiru.sponge.boxboy.Boxboy
import com.github.xemiru.sponge.boxboy.Menu
import com.github.xemiru.sponge.boxboy.button.ActionButton
import flavor.pie.kludge.*
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.spec.CommandExecutor
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.item.ItemTypes
import org.spongepowered.api.world.Location

class BoxboySupport {

    companion object {
        lateinit var instance: BoxboySupport

        fun hook() {
            instance = BoxboySupport()
            instance.boxboy = Boxboy.get().also { TitleZones.logger.info("Boxboy detected - setting up Zone Menu") }
            instance.setupMenu()
        }
    }

    private lateinit var boxboy: Boxboy
    private lateinit var zoneMenu: Menu

    fun setupMenu() {
        val zs = TitleZones.config.zones

        zoneMenu = boxboy.createMenu(if (zs.size % 9 > 0) (zs.size / 9) + 1 else zs.size / 9, !"Title Zones")

        for (i in 0 until TitleZones.config.zones.size) {
            val z = TitleZones.config.zones[i]

            zoneMenu.setButton(i, ActionButton.of(itemStackOf {
                add(Keys.DISPLAY_NAME, z.title)
                add(Keys.ITEM_LORE, listOf(
                        z.subtitle,
                        !"X: ${z.position.x}".gray(),
                        !"Y: ${z.position.y}".gray(),
                        !"Z: ${z.position.z}".gray()
                ))
                itemType(ItemTypes.ENDER_PEARL)
            }, { ctx ->
                with(ctx.clicker) {
                    if (!ctx.clicker.closeInventory())
                        TitleZones.logger.warn("Wasn't able to close inventory before teleporting")

                    setLocationSafely(Location(location.extent, z.position))
                }
            }))
        }

        CommandManager.register(TitleZones.instance, commandSpecOf {
            permission("titlezones.menu")
            executor(
                    CommandExecutor { src, _ ->
                        (src as? Player)?.let { p ->
                            zoneMenu.open(p)
                            return@CommandExecutor CommandResult.success()
                        }

                        return@CommandExecutor CommandResult.empty()
                    })
        }, "zm")
    }

}