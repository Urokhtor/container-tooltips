package io.urokhtor.minecraft.containertooltips.configuration;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import org.lwjgl.glfw.GLFW;

@Config(name = "container-tooltips")
public final class Configuration implements ConfigData {

    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean showAutomatically = true;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public int showWithKeyCode = GLFW.GLFW_KEY_LEFT_SHIFT;
}
