package io.urokhtor.minecraft.containertooltips.rendering

import io.urokhtor.minecraft.containertooltips.Container
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil
import net.minecraft.network.chat.Component
import net.minecraft.util.CommonColors

class EmptyContainerTooltip {
    fun render(font: Font, horizontalCenter: Int, guiGraphics: GuiGraphicsExtractor, container: Container) {
        TooltipRenderUtil.extractTooltipBackground(
            guiGraphics,
            horizontalCenter - font.width(container.name) / 2,
            Y_START,
            font.width(container.name),
            font.lineHeight * 2,
            null
        )

        guiGraphics.text(
            font,
            container.name,
            horizontalCenter - font.width(container.name) / 2,
            Y_START,
            CommonColors.WHITE,
            true
        )
        guiGraphics.text(
            font,
            Component.translatable("container.empty"),
            horizontalCenter - font.width(container.name) / 2,
            Y_START + font.lineHeight + TOOLTIP_BACKGROUND_PADDING,
            CommonColors.GRAY,
            true
        )
    }
}
