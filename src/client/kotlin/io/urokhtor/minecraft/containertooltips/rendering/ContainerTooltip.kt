package io.urokhtor.minecraft.containertooltips.rendering

import io.urokhtor.minecraft.containertooltips.Container
import net.minecraft.client.font.TextRenderer
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
            getWidth(container),
            getHeight(container) + textRenderer.fontHeight + PADDING,
            0
        )

        drawContext.drawText(textRenderer, container.name, x, Y_START, Colors.WHITE, true)

        val backgroundTextureYStart = Y_START + textRenderer.fontHeight + PADDING

        drawContext.drawGuiTexture(
            BACKGROUND_TEXTURE,
            x,
            backgroundTextureYStart,
            this.getWidth(container),
            this.getHeight(container)
        )

        container.inventory.chunked(getItemsOnOneRow(container))
            .forEachIndexed { stackIndex, itemStacks ->
                itemStacks.forEachIndexed { itemIndex, itemStack ->
                    val xOffset = x + itemIndex * ITEM_SIZE_X + 1
                    val yOffset = backgroundTextureYStart + stackIndex * ITEM_SIZE_Y + 1
                    drawContext.drawGuiTexture(SLOT_TEXTURE, xOffset, yOffset, 0, ITEM_SIZE_X, ITEM_SIZE_Y)
                    drawContext.drawItem(itemStack, xOffset + 1, yOffset + 1)
                    drawContext.drawItemInSlot(textRenderer, itemStack, xOffset + 1, yOffset + 1)
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
