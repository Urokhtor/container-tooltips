package io.urokhtor.minecraft.containertooltips.rendering

import io.urokhtor.minecraft.containertooltips.Container
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gl.RenderPipelines
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.tooltip.TooltipBackgroundRenderer
import net.minecraft.util.Colors
import kotlin.math.roundToInt

class ContainerTooltip {
    fun render(textRenderer: TextRenderer, horizontalCenter: Int, drawContext: DrawContext, container: Container) {
        val x = horizontalCenter - (getItemsOnOneRow(container) / 2.0 * ITEM_SIZE_X).toInt()
        TooltipBackgroundRenderer.render(
            drawContext,
            x,
            Y_START,
            getWidth(container) + TOOLTIP_BACKGROUND_PADDING,
            getHeight(container) + textRenderer.fontHeight + TOOLTIP_BACKGROUND_PADDING * 2,
            null
        )

        drawContext.drawText(textRenderer, container.name, x, Y_START, Colors.WHITE, true)

        val backgroundTextureYStart = Y_START + textRenderer.fontHeight + TOOLTIP_BACKGROUND_PADDING

        drawContext.fill(
            x,
            backgroundTextureYStart,
            x + this.getWidth(container) + TOOLTIP_BACKGROUND_PADDING,
            backgroundTextureYStart + this.getHeight(container) + TOOLTIP_BACKGROUND_PADDING,
            Colors.LIGHT_GRAY
        )

        container.inventory.chunked(getItemsOnOneRow(container))
            .forEachIndexed { stackIndex, itemStacks ->
                itemStacks.forEachIndexed { itemIndex, itemStack ->
                    val xOffset = x + itemIndex * ITEM_SIZE_X + TOOLTIP_BACKGROUND_PADDING
                    val yOffset = backgroundTextureYStart + stackIndex * ITEM_SIZE_Y + TOOLTIP_BACKGROUND_PADDING
                    drawContext.drawGuiTexture(
                        RenderPipelines.GUI_TEXTURED,
                        SLOT_TEXTURE,
                        xOffset,
                        yOffset,
                        ITEM_SIZE_X,
                        ITEM_SIZE_Y
                    )
                    drawContext.drawItem(itemStack, xOffset + 1, yOffset + 1)
                    drawContext.drawStackOverlay(textRenderer, itemStack, xOffset + 1, yOffset + 1)
                }
            }
    }

    private fun getWidth(container: Container): Int {
        val itemsOnOneRow = getItemsOnOneRow(container)
        return itemsOnOneRow * ITEM_SIZE_X + 2
    }

    private fun getHeight(container: Container): Int {
        return rowCount(container) * ITEM_SIZE_Y + 2
    }

    private fun rowCount(container: Container): Int {
        return if (container.inventory.size < 9) 1 else (container.inventory.size / 9.0).roundToInt()
    }

    private fun getItemsOnOneRow(container: Container) = if (container.inventory.size < 9) container.inventory.size else MAX_ROW_LENGTH
}
