package io.urokhtor.minecraft

import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.tooltip.TooltipBackgroundRenderer
import net.minecraft.client.gui.tooltip.TooltipComponent
import net.minecraft.util.Colors
import net.minecraft.util.Identifier
import kotlin.math.roundToInt

private const val MAX_ROW_LENGTH: Int = 9
const val ITEM_SIZE_X: Int = 18
private const val ITEM_SIZE_Y: Int = 20
private const val TEXTURE_WIDTH: Int = 128
private const val PADDING: Int = 3
private val BACKGROUND_TEXTURE = Identifier("textures/gui/container/bundle.png")

class InventoryTooltipComponent(private val container: Container) : TooltipComponent {

    override fun getWidth(textRenderer: TextRenderer?): Int {
        val itemsOnOneRow = getItemsOnOneRow()
        return itemsOnOneRow * ITEM_SIZE_X + 2
    }

    override fun getHeight(): Int {
        return rowCount() * ITEM_SIZE_Y + 2
    }

    private fun rowCount(): Int {
        return if (container.inventory.size < 9) 1 else (container.inventory.size / 9.0).roundToInt()
    }

    fun getItemsOnOneRow() = if (container.inventory.size < 9) container.inventory.size else MAX_ROW_LENGTH

    override fun drawItems(textRenderer: TextRenderer, x: Int, y: Int, drawContext: DrawContext) {
        TooltipBackgroundRenderer.render(
            drawContext,
            x,
            y,
            getWidth(textRenderer),
            height + textRenderer.fontHeight + PADDING,
            0
        )

        drawContext.drawText(textRenderer, container.name, x, y, Colors.WHITE, true)

        val backgroundTextureYStart = y + textRenderer.fontHeight + PADDING

        container.inventory.chunked(getItemsOnOneRow())
            .forEachIndexed { stackIndex, itemStacks ->
                itemStacks.forEachIndexed { itemIndex, itemStack ->
                    val xOffset = x + itemIndex * ITEM_SIZE_X + 1
                    val yOffset = backgroundTextureYStart + stackIndex * ITEM_SIZE_Y + 1
                    drawContext.drawTexture(
                        BACKGROUND_TEXTURE,
                        xOffset,
                        yOffset,
                        0,
                        0f,
                        0f,
                        ITEM_SIZE_X,
                        ITEM_SIZE_Y,
                        TEXTURE_WIDTH,
                        TEXTURE_WIDTH
                    )
                    drawContext.drawItem(itemStack, xOffset + 1, yOffset + 1)
                    drawContext.drawItemInSlot(textRenderer, itemStack, xOffset + 1, yOffset + 1)
                }
            }
    }
}
