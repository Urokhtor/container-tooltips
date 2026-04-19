package io.urokhtor.minecraft.containertooltips.mixin.client;

import io.urokhtor.minecraft.containertooltips.ScreenEventsKt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public class ScreenMixin {

    @Inject(at = @At("TAIL"), method = "init(II)V")
    private void init(int width, int height, CallbackInfo callbackInfo) {
        ScreenEventsKt.getSCREEN_OPENED().invoker().onScreenOpened(Minecraft.getInstance());
    }

    @Inject(at = @At("TAIL"), method = "onClose")
    private void onClose(CallbackInfo callbackInfo) {
        ScreenEventsKt.getSCREEN_CLOSED().invoker().onScreenClosed(Minecraft.getInstance());
    }
}
