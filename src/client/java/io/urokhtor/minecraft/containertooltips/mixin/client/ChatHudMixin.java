package io.urokhtor.minecraft.containertooltips.mixin.client;

import io.urokhtor.minecraft.containertooltips.ScreenEventsKt;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.screen.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatHud.class)
public class ChatHudMixin {

    @Inject(at = @At("TAIL"), method = "removeScreen")
    private void removeScreen(CallbackInfoReturnable<ChatScreen> cir) {
        ScreenEventsKt.getSCREEN_CLOSED().invoker().onScreenClosed(MinecraftClient.getInstance());
    }
}
