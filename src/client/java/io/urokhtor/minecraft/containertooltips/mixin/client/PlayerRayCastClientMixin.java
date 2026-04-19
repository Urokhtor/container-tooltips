package io.urokhtor.minecraft.containertooltips.mixin.client;

import io.urokhtor.minecraft.containertooltips.InventoryRequest;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(LocalPlayer.class)
public abstract class PlayerRayCastClientMixin {

	@Unique
	private InventoryRequest.StaringContext staringContext;

	@Inject(at = @At("RETURN"), method = "raycastHitResult")
	private void onRayCast(final float a, final Entity cameraEntity, CallbackInfoReturnable<HitResult> callbackInfoReturnable) {
		if (callbackInfoReturnable.getReturnValue() instanceof BlockHitResult hitResult) {
			staringContext = InventoryRequest.INSTANCE.handleInventoryRequest(hitResult, staringContext, Objects.requireNonNull(Minecraft.getInstance().level));
		}
	}
}
