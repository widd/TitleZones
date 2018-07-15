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

import com.google.inject.Inject
import ninja.leaping.configurate.commented.CommentedConfigurationNode
import ninja.leaping.configurate.loader.ConfigurationLoader
import org.slf4j.Logger
import org.spongepowered.api.config.DefaultConfig
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.entity.MoveEntityEvent
import org.spongepowered.api.event.filter.Getter
import org.spongepowered.api.event.game.GameReloadEvent
import org.spongepowered.api.event.game.state.GamePreInitializationEvent
import org.spongepowered.api.plugin.Plugin
import org.spongepowered.api.plugin.PluginContainer
import org.spongepowered.api.text.title.Title
import java.nio.file.Files
import java.nio.file.Path

@Plugin(id="titlezones",
        name="Title Zones",
        description="A Sponge plugin that allows you to define zones that send Titles to Players who cross their borders.",
        version="1.0.0-SNAPSHOT",
        authors=["widd"])
class TitleZones @Inject constructor(private val plugin: PluginContainer,
                                     private val logger: Logger,
                                     @DefaultConfig(sharedRoot=false) private val path: Path,
                                     @DefaultConfig(sharedRoot=false) private val loader: ConfigurationLoader<CommentedConfigurationNode>) {
    companion object {
        lateinit var instance: TitleZones
        val config get() = instance.config
        val logger get() = instance.logger
    }

    private lateinit var config: Config

    init {
        instance = this
    }

    private fun loadConfig() {
        if (!Files.exists(path)) {
            logger.info("Config not found, creating defaults")
            with (loader) {
                save(createEmptyNode().setValue(Config.type, Config()))
            }
        }

        val opts = loader.defaultOptions
        config = loader.load(opts).getValue(Config.type)
        logger.info("Config loaded")
        logger.info("${config.zones.size} zone(s) loaded")
        config.zones.forEach {
            logger.info(it.title.toPlain())
        }
    }

    @Listener
    fun preInit(e: GamePreInitializationEvent) {
        loadConfig()
    }

    @Listener
    fun reload(e: GameReloadEvent) {
        loadConfig()
    }

    @Listener
    fun enumerateZones(e: MoveEntityEvent,
                       @Getter("getTargetEntity") p: Player) {
        config.zones
                .filter { it.position.distance(e.fromTransform.position) > it.radius }
                .filter { it.position.distance(e.toTransform.position) <= it.radius }
                .forEach {
                    val title = Title.builder().title(it.title).subtitle(it.subtitle).build()
                    p.sendTitle(title)
                }
    }

}