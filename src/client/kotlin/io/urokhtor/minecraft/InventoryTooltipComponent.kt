package io.urokhtor.minecraft

import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.tooltip.TooltipComponent
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.minecraft.util.collection.DefaultedList
import kotlin.math.roundToInt

private const val MAX_ROW_LENGTH: Int = 9
const val ITEM_SIZE_X: Int = 18
private const val ITEM_SIZE_Y: Int = 20
private val BACKGROUND_TEXTURE = Identifier("container/bundle/background")
private val SLOT_TEXTURE = Identifier("container/bundle/slot")

class InventoryTooltipComponent(private val inventory: DefaultedList<ItemStack>) : TooltipComponent {

    override fun getWidth(textRenderer: TextRenderer?): Int {
        return getColumnsWidth()
    }

    override fun getHeight(): Int {
        return getRowsHeight() + 4
    }

    private fun getColumnsWidth(): Int {
        val itemsOnOneRow = getItemsOnOneRow()
        return itemsOnOneRow * ITEM_SIZE_X + 2
    }

    private fun getRowsHeight(): Int {
        return (inventory.size / 9.0).roundToInt() * ITEM_SIZE_Y + 2
    }

    fun getItemsOnOneRow() = if (inventory.size < 9) inventory.size else MAX_ROW_LENGTH

    override fun drawItems(textRenderer: TextRenderer, x: Int, y: Int, drawContext: DrawContext) {
        drawContext.drawGuiTexture(
            BACKGROUND_TEXTURE,
            x,
            y,
            this.getColumnsWidth(),
            this.getRowsHeight()
        )

        inventory.chunked(getItemsOnOneRow())
            .forEachIndexed { stackIndex, itemStacks ->
                itemStacks.forEachIndexed { itemIndex, itemStack ->
                    val xOffset = x + itemIndex * ITEM_SIZE_X + 1
                    val yOffset = y + stackIndex * ITEM_SIZE_Y + 1
                    drawContext.drawGuiTexture(SLOT_TEXTURE, xOffset, yOffset, 0, ITEM_SIZE_X, ITEM_SIZE_Y)
                    drawContext.drawItem(itemStack, xOffset + 1, yOffset + 1)
                    drawContext.drawItemInSlot(textRenderer, itemStack, xOffset + 1, yOffset + 1)
                }
            }
    }
}
