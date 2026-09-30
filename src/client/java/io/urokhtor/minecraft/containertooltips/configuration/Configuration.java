package io.urokhtor.minecraft.containertooltips.configuration;

import com.mojang.blaze3d.platform.InputConstants;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.clothconfig2.api.Modifier;
import me.shedaniel.clothconfig2.api.ModifierKeyCode;

@Config(name = "container-tooltips")
public final class Configuration implements ConfigData {

    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean showAutomatically = true;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public ModifierKeyCode showWithKeyCode = ModifierKeyCode.of(
            InputConstants.Type.KEYBOARD.getOrCreate(InputConstants.KEY_LSHIFT),
            Modifier.none()
    );
}
