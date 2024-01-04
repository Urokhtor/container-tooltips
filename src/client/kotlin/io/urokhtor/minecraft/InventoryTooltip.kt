package io.urokhtor.minecraft

import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner
import net.minecraft.item.AirBlockItem
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.collection.DefaultedList

private const val Y_START: Int = 5

class InventoryTooltip {
    fun render(textRenderer: TextRenderer, horizontalCenter: Int, drawContext: DrawContext, inventory: DefaultedList<ItemStack>) {
        val isInventoryEmpty = inventory
            .none { it.item !is AirBlockItem }

        if (isInventoryEmpty) {
            val emptyContainerMessage = Text.translatable("container.empty").asOrderedText()
            drawContext.drawTooltip(
                textRenderer,
                listOf(emptyContainerMessage),
                HoveredTooltipPositioner.INSTANCE,
                horizontalCenter - textRenderer.getWidth(emptyContainerMessage),
                Y_START * 2 + textRenderer.fontHeight
            )
            return
        }

        val inventoryTooltipComponent = InventoryTooltipComponent(inventory)
        val itemsOnOneRow = inventoryTooltipComponent.getItemsOnOneRow()
        val xStart = horizontalCenter - (itemsOnOneRow / 2.0 * ITEM_SIZE_X).toInt()
        inventoryTooltipComponent.drawItems(textRenderer, xStart, Y_START, drawContext)
    }
}
