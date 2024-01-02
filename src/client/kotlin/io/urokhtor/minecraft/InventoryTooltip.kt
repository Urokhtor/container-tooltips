package io.urokhtor.minecraft

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner
import net.minecraft.client.gui.tooltip.TooltipBackgroundRenderer
import net.minecraft.item.AirBlockItem
import net.minecraft.item.ItemStack
import net.minecraft.text.Text

private const val MAX_ROW_LENGTH: Int = 9
private const val ITEM_SIZE: Int = 16
private const val Y_START: Int = 16

class InventoryTooltip {
    fun render(client: MinecraftClient, drawContext: DrawContext, inventory: List<ItemStack>) {
        val inventoryWithoutAir = inventory
            .filter { it.item !is AirBlockItem }

        if (inventoryWithoutAir.isEmpty()) {
            val emptyContainerMessage = Text.of("Empty").asOrderedText()
            drawContext.drawTooltip(
                client.textRenderer,
                listOf(emptyContainerMessage),
                HoveredTooltipPositioner.INSTANCE,
                client.window.scaledWidth / 2 - client.textRenderer.getWidth(emptyContainerMessage),
                Y_START * 2
            )
            return
        }

        val itemsOnOneRow = if (inventoryWithoutAir.size < 9) inventoryWithoutAir.size else MAX_ROW_LENGTH

        val inventoryChunkedIntoRows = inventoryWithoutAir.chunked(itemsOnOneRow)

        val xStart = client.window.scaledWidth / 2 - (itemsOnOneRow / 2.0 * ITEM_SIZE).toInt()

        TooltipBackgroundRenderer.render(drawContext, xStart, Y_START, ITEM_SIZE * itemsOnOneRow, ITEM_SIZE * inventoryChunkedIntoRows.size, 5)
        inventoryChunkedIntoRows
            .forEachIndexed { stackIndex, itemStacks ->
                itemStacks.forEachIndexed { itemIndex, itemStack ->
                    val xOffset = xStart + (itemIndex * ITEM_SIZE)
                    val yOffset = Y_START + stackIndex * ITEM_SIZE
                    drawContext.drawItem(itemStack, xOffset, yOffset)
                    drawContext.drawItemInSlot(client.textRenderer, itemStack, xOffset, yOffset)
                }
            }
    }
}
