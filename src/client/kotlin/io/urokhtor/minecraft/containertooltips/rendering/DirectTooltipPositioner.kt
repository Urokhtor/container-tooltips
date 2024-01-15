package io.urokhtor.minecraft.containertooltips.rendering

import net.minecraft.client.gui.tooltip.TooltipPositioner
import org.joml.Vector2i
import org.joml.Vector2ic

/**
 * Implementation of [TooltipPositioner] that does not adjust tooltip position in any way. In this mod's case we want to
 * place the tooltip precisely to the calculated x and y values. Minecraft's own tooltip positioners do all kinds of
 * adjustments to the tooltip position, making it hard to position the tooltip exactly at the middle and at the right
 * y position.
 */
class DirectTooltipPositioner : TooltipPositioner {
    override fun getPosition(screenWidth: Int, screenHeight: Int, x: Int, y: Int, width: Int, height: Int): Vector2ic {
        return Vector2i(x, y)
    }
}
