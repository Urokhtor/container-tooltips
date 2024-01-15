package io.urokhtor.minecraft.containertooltips.configuration;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "container-tooltips")
public final class Configuration implements ConfigData {

    @Comment("When on, tooltip is shown automatically. Otherwise, Left Shift must be pressed to show the tooltip.")
    public boolean showAutomatically = true;
}
