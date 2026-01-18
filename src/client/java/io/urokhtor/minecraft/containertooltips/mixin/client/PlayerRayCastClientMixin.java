package io.urokhtor.minecraft.containertooltips.mixin.client;

import io.urokhtor.minecraft.containertooltips.InventoryRequest;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class PlayerRayCastClientMixin {

	@Shadow public abstract World getEntityWorld();

	@Unique
	private InventoryRequest.StaringContext staringContext;

	@Inject(at = @At("RETURN"), method = "raycast")
	private void onRayCast(CallbackInfoReturnable<HitResult> callbackInfoReturnable) {
		if (callbackInfoReturnable.getReturnValue() instanceof BlockHitResult hitResult) {
			staringContext = InventoryRequest.INSTANCE.handleInventoryRequest(hitResult, staringContext, this.getEntityWorld());
		}
	}
}
