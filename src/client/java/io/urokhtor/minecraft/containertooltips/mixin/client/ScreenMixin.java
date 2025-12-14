package io.urokhtor.minecraft.containertooltips.mixin.client;

import io.urokhtor.minecraft.containertooltips.InventoryEventsKt;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public class ScreenMixin {

    @Inject(at = @At("TAIL"), method = "init(II)V")
    private void init(int width, int height, CallbackInfo callbackInfo) {
        InventoryEventsKt.getINVENTORY_OPENED().invoker().onInventoryOpened(MinecraftClient.getInstance());
    }

    @Inject(at = @At("TAIL"), method = "close")
    private void close(CallbackInfo callbackInfo) {
        InventoryEventsKt.getINVENTORY_CLOSED().invoker().onInventoryClosed(MinecraftClient.getInstance());
    }
}
