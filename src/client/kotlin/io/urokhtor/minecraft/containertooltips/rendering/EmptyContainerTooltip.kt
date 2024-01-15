package io.urokhtor.minecraft.containertooltips.rendering

import io.urokhtor.minecraft.containertooltips.Container
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import net.minecraft.util.Colors

class EmptyContainerTooltip {
    fun render(textRenderer: TextRenderer, horizontalCenter: Int, drawContext: DrawContext, container: Container) {
        drawContext.drawTooltip(
            textRenderer,
            listOf(
                Text.of(container.name),
                Text.translatable("container.empty").withColor(Colors.GRAY)
            ),
            horizontalCenter - textRenderer.getWidth(container.name),
            Y_START + textRenderer.fontHeight
        )
    }
}
