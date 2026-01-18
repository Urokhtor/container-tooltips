package io.urokhtor.minecraft.containertooltips.mixin.client;

import io.urokhtor.minecraft.containertooltips.InventoryRequest;
import net.minecraft.component.type.AttackRangeComponent;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(AttackRangeComponent.class)
public abstract class AttackRangeComponentMixin {

	@Unique
	private InventoryRequest.StaringContext staringContext;

	@Inject(at = @At("RETURN"), method = "getHitResult")
	private void getHitResult(Entity entity, float tickProgress, Predicate<Entity> hitPredicate, CallbackInfoReturnable<HitResult> callbackInfoReturnable) {
		if (callbackInfoReturnable.getReturnValue() instanceof BlockHitResult hitResult) {
			// No need to do anything here because in this case the game does normal raycast in any case.
			if (hitResult.getType() == HitResult.Type.MISS) {
				return;
			}

			staringContext = InventoryRequest.INSTANCE.handleInventoryRequest(hitResult, staringContext, entity.getEntityWorld());
		}
	}
}
