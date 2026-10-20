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
 */

package foo.starred.athen.api.dungeon.data

enum class DungeonFloor(
    val bossName: String,
    val chatBossName: String = bossName,
    val floorNumber: Int,
    val longName: String,
) {
    E("The Watcher", 0, "The Catacombs Entrance"),

    F1("Bonzo", 1, "The Catacombs Floor I"),
    F2("Scarf", 2, "The Catacombs Floor II"),
    F3("The Professor", 3, "The Catacombs Floor III"),
    F4("Thorn", 4, "The Catacombs Floor IV"),
    F5("Livid", 5, "The Catacombs Floor V"),
    F6("Sadan", 6, "The Catacombs Floor VI"),
    F7("Necron", "Maxor", 7, "The Catacombs Floor VII"),

    M1("Bonzo", 1, "Master Mode The Catacombs Floor I"),
    M2("Scarf", 2, "Master Mode The Catacombs Floor II"),
    M3("The Professor", 3, "Master Mode The Catacombs Floor III"),
    M4("Thorn", 4, "Master Mode The Catacombs Floor IV"),
    M5("Livid", 5, "Master Mode The Catacombs Floor V"),
    M6("Sadan", 6, "Master Mode The Catacombs Floor VI"),
    M7("Necron", "Maxor", 7, "Master Mode The Catacombs Floor VII"),
    ;

    constructor(bossName: String, floorNumber: Int, longName: String) : this(bossName, bossName, floorNumber, longName)

    companion object {
        fun getByName(name: String) = entries.firstOrNull { it.name.uppercase() == name }
        fun getByLongName(name: String) = DungeonFloor.entries.firstOrNull { it.longName == name }
    }
}
