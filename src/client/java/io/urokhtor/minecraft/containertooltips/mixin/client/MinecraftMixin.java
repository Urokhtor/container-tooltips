package io.urokhtor.minecraft.containertooltips.mixin.client;

import io.urokhtor.minecraft.containertooltips.ScreenEventsKt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Inject(at = @At("TAIL"), method = "setScreen")
    private void setScreen(Screen screen, CallbackInfo callbackInfo) {
        if (screen == null) {
            ScreenEventsKt.getSCREEN_CLOSED().invoker().onScreenClosed(Minecraft.getInstance());
        }
    }
}
