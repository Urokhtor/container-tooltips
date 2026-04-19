package io.urokhtor.minecraft.containertooltips.rendering

import io.urokhtor.minecraft.containertooltips.Container
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.util.CommonColors
import kotlin.math.roundToInt

class ContainerTooltip {
    fun render(font: Font, horizontalCenter: Int, guiGraphics: GuiGraphicsExtractor, container: Container) {
        val x = horizontalCenter - (getItemsOnOneRow(container) / 2.0 * ITEM_SIZE_X).toInt()
        TooltipRenderUtil.extractTooltipBackground(
            guiGraphics,
            x,
            Y_START,
            getWidth(container) + TOOLTIP_BACKGROUND_PADDING,
            getHeight(container) + font.lineHeight + TOOLTIP_BACKGROUND_PADDING * 2,
            null
        )

        guiGraphics.text(font, container.name, x, Y_START, CommonColors.WHITE, true)

        val backgroundTextureYStart = Y_START + font.lineHeight + TOOLTIP_BACKGROUND_PADDING

        guiGraphics.fill(
            x,
            backgroundTextureYStart,
            x + this.getWidth(container) + TOOLTIP_BACKGROUND_PADDING,
            backgroundTextureYStart + this.getHeight(container) + TOOLTIP_BACKGROUND_PADDING,
            CommonColors.LIGHT_GRAY
        )

        container.inventory.chunked(getItemsOnOneRow(container))
            .forEachIndexed { stackIndex, itemStacks ->
                itemStacks.forEachIndexed { itemIndex, itemStack ->
                    val xOffset = x + itemIndex * ITEM_SIZE_X + TOOLTIP_BACKGROUND_PADDING
                    val yOffset = backgroundTextureYStart + stackIndex * ITEM_SIZE_Y + TOOLTIP_BACKGROUND_PADDING
                    guiGraphics.blitSprite(
                        RenderPipelines.GUI_TEXTURED,
                        SLOT_TEXTURE,
                        xOffset,
                        yOffset,
                        ITEM_SIZE_X,
                        ITEM_SIZE_Y
                    )
                    guiGraphics.item(itemStack, xOffset + 1, yOffset + 1)
                    guiGraphics.itemDecorations(font, itemStack, xOffset + 1, yOffset + 1)
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
