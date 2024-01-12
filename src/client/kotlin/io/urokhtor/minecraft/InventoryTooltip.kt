package io.urokhtor.minecraft

import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.item.AirBlockItem
import net.minecraft.text.Text

private const val Y_START: Int = 10

class InventoryTooltip {
    fun render(textRenderer: TextRenderer, horizontalCenter: Int, drawContext: DrawContext, container: Container) {
        val isInventoryEmpty = container.inventory
            .none { it.item !is AirBlockItem }

        if (isInventoryEmpty) {
            drawContext.drawTooltip(
                textRenderer,
                Text.of(container.name),
                horizontalCenter - textRenderer.getWidth(container.name),
                Y_START + textRenderer.fontHeight
            )
            return
        }

        val inventoryTooltipComponent = InventoryTooltipComponent(container)
        val xStart = horizontalCenter - (inventoryTooltipComponent.getItemsOnOneRow() / 2.0 * ITEM_SIZE_X).toInt()
        inventoryTooltipComponent.drawItems(textRenderer, xStart, Y_START, drawContext)
    }
}
