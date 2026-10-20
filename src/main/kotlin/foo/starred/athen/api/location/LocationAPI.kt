/*
 * Original work by [SkyblockAPI](https://github.com/SkyblockAPI/SkyblockAPI) and contributors (MIT License).
 * The MIT License (MIT)
 *
 * Copyright (c) 2025
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
 *
 * Modifications:
 *   Copyright (c) 2025 skies-starred
 *   Licensed under the BSD 3-Clause License.
 *
 * The original MIT license applies to the portions derived from SkyblockAPI.
 */
@file:Suppress("UNUSED")

package foo.starred.athen.api.location

import foo.starred.athen.annotations.impl.LoadableAthenModule
import foo.starred.athen.api.location.area.base.ISkyBlockArea
import foo.starred.athen.api.location.area.impl.CustomSkyBlockArea
import foo.starred.athen.api.location.area.impl.PresetSkyBlockArea
import foo.starred.athen.api.location.island.base.ISkyBlockIsland
import foo.starred.athen.api.location.island.impl.CustomSkyBlockIsland
import foo.starred.athen.api.location.island.impl.PresetSkyBlockIsland
import foo.starred.athen.events.LocationEvent
import foo.starred.athen.events.ScoreboardEvent
import foo.starred.athen.events.core.on
import foo.starred.kbus.extensions.runWhen
import foo.starred.kontext.api.data.Observable
import net.hypixel.data.type.GameType

@LoadableAthenModule
object LocationAPI {
    private val regex = Regex(" *[⏣ф\uE067\uE020] *(?<location>(?:\\s?[^[ൠ\uE018]\\s]+)*)(?: [ൠ\uE018] x\\d)?")

    val skyblock: Observable<Boolean> = Observable(false).observe { (if (it) LocationEvent.SkyBlock.Connect else LocationEvent.SkyBlock.Disconnect).post() }
    val island: Observable<ISkyBlockIsland> = Observable(PresetSkyBlockIsland.NONE)
    val area: Observable<ISkyBlockArea> = Observable(PresetSkyBlockArea.NONE)

    var id: String? = null
        private set

    init {
        on<LocationEvent.Hypixel.Server> {
            skyblock.value = type == GameType.SKYBLOCK

            if (!skyblock.value || mode == null) {
                val v0 = island.value

                id = name
                island.value = PresetSkyBlockIsland.NONE
                LocationEvent.SkyBlock.Island(v0, island.value).post()
                return@on
            }

            val v0 = island.value
            val v1 = PresetSkyBlockIsland.of(mode) ?: CustomSkyBlockIsland(mode)

            id = name
            island.value = v1
            LocationEvent.SkyBlock.Island(v0, v1).post()
        }

        on<ScoreboardEvent.Update> {
            for (line in added) {
                val (location) = regex.find(line)?.destructured ?: continue
                val old = area.value

                area.value = PresetSkyBlockArea.of(location) ?: CustomSkyBlockArea(location)
                LocationEvent.SkyBlock.Area(old, area.value).post()
                break
            }
        }.runWhen(skyblock)

        on<LocationEvent.Server.Disconnect> {
            reset()
        }
    }

    private fun reset() {
        val v00 = area.value
        val v01 = island.value

        id = null
        skyblock.value = false
        island.value = PresetSkyBlockIsland.NONE
        area.value = PresetSkyBlockArea.NONE

        if (v00 != PresetSkyBlockArea.NONE) LocationEvent.SkyBlock.Area(v00, PresetSkyBlockArea.NONE).post()
        if (v01 != PresetSkyBlockIsland.NONE) LocationEvent.SkyBlock.Island(v01, PresetSkyBlockIsland.NONE).post()
    }
}
