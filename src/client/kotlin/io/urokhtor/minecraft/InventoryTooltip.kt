package io.urokhtor.minecraft

import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.item.AirBlockItem
import net.minecraft.text.Text

/**
 * Hovered tooltip positioner offsets tooltip 12 pixels up, and tooltip background 3 pixels more. This constant is used
 * to reset the offset.
 */
private const val TOOLTIP_Y_OFFSET: Int = 15

/**
 * Hovered tooltip positioner offsets tooltip text 12 pixels to the right, and tooltip background subtracts 3 pixels.
 * This constant is used to align the tooltip border with inventory background texture.
 */
private const val TOOLTIP_X_OFFSET: Int = 9

class InventoryTooltip {
    fun render(textRenderer: TextRenderer, horizontalCenter: Int, drawContext: DrawContext, container: Container) {
        val inventoryTooltipComponent = InventoryTooltipComponent(container)
        val xStart = horizontalCenter - (inventoryTooltipComponent.getItemsOnOneRow() / 2.0 * ITEM_SIZE_X).toInt()

        val tooltipBottomPosition = TOOLTIP_Y_OFFSET + textRenderer.fontHeight

        drawContext.drawTooltip(
            textRenderer,
            Text.of(container.name),
            xStart - TOOLTIP_X_OFFSET,
            tooltipBottomPosition
        )

        val inventoryHasItems = container.inventory
            .any { it.item !is AirBlockItem }

        if (inventoryHasItems) {
            inventoryTooltipComponent.drawItems(textRenderer, xStart, tooltipBottomPosition, drawContext)
        }
    }
}
