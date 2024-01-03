package io.urokhtor.minecraft

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner
import net.minecraft.item.AirBlockItem
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.collection.DefaultedList

private const val Y_START: Int = 5

class InventoryTooltip {
    fun render(client: MinecraftClient, drawContext: DrawContext, inventory: DefaultedList<ItemStack>) {
        val isInventoryEmpty = inventory
            .none { it.item !is AirBlockItem }

        if (isInventoryEmpty) {
            val emptyContainerMessage = Text.translatable("container.empty").asOrderedText()
            drawContext.drawTooltip(
                client.textRenderer,
                listOf(emptyContainerMessage),
                HoveredTooltipPositioner.INSTANCE,
                client.window.scaledWidth / 2 - client.textRenderer.getWidth(emptyContainerMessage),
                Y_START * 2 + client.textRenderer.fontHeight
            )
            return
        }

        val inventoryTooltipComponent = InventoryTooltipComponent(inventory)
        val itemsOnOneRow = inventoryTooltipComponent.getItemsOnOneRow()
        val xStart = client.window.scaledWidth / 2 - (itemsOnOneRow / 2.0 * ITEM_SIZE_X).toInt()
        inventoryTooltipComponent.drawItems(client.textRenderer, xStart, Y_START, drawContext)
    }
}
