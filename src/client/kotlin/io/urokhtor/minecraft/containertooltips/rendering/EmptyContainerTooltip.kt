package io.urokhtor.minecraft.containertooltips.rendering

import io.urokhtor.minecraft.containertooltips.Container
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.tooltip.TooltipBackgroundRenderer
import net.minecraft.text.Text
import net.minecraft.util.Colors

class EmptyContainerTooltip {
    fun render(textRenderer: TextRenderer, horizontalCenter: Int, drawContext: DrawContext, container: Container) {
        TooltipBackgroundRenderer.render(
            drawContext,
            horizontalCenter - textRenderer.getWidth(container.name) / 2,
            Y_START,
            textRenderer.getWidth(container.name),
            textRenderer.fontHeight * 2,
            null
        )

        drawContext.drawText(
            textRenderer,
            container.name,
            horizontalCenter - textRenderer.getWidth(container.name) / 2,
            Y_START,
            Colors.WHITE,
            true
        )
        drawContext.drawText(
            textRenderer,
            Text.translatable("container.empty"),
            horizontalCenter - textRenderer.getWidth(container.name) / 2,
            Y_START + textRenderer.fontHeight + TOOLTIP_BACKGROUND_PADDING,
            Colors.GRAY,
            true
        )
    }
}
