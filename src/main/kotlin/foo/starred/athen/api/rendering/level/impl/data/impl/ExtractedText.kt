package foo.starred.athen.api.rendering.level.impl.data.impl

import net.minecraft.network.chat.Component
import net.minecraft.world.phys.Vec3
import foo.starred.athen.api.rendering.level.impl.data.base.ILevelExtractable

data class ExtractedText(
    val text: Component,
    val pos: Vec3,
    val color0: Int,
    val color1: Int,
    val scale: Float,
    val shadow: Boolean,
    val depth: Boolean
) : ILevelExtractable