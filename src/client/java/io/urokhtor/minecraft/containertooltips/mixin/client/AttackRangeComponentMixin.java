package io.urokhtor.minecraft.containertooltips.mixin.client;

import io.urokhtor.minecraft.containertooltips.CurrentContainerContext;
import io.urokhtor.minecraft.containertooltips.InventoryRequestPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.component.type.AttackRangeComponent;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.time.Instant;
import java.util.function.Predicate;

@Mixin(AttackRangeComponent.class)
public abstract class AttackRangeComponentMixin {
	@Unique
	private BlockPos lastPositionStaredAt;

	@Unique
	private Instant lastPollInstant = Instant.now();

	@Inject(at = @At("RETURN"), method = "getHitResult")
	private void getHitResult(Entity entity, float tickProgress, Predicate<Entity> hitPredicate, CallbackInfoReturnable<HitResult> callbackInfoReturnable) {
		if (callbackInfoReturnable.getReturnValue() instanceof BlockHitResult hitResult) {
			// No need to do anything here because in this case the game does normal raycast in any case.
			if (hitResult.getType() == HitResult.Type.MISS) {
				return;
			}

			BlockPos blockPosition = hitResult.getBlockPos();
			Instant currentInstant = Instant.now();

			// Don't send network request if we have already requested container's contents, and it has been under 1
			// since the last time we polled the contents.
			if (blockPosition.equals(lastPositionStaredAt) && currentInstant.minusSeconds(1).isBefore(lastPollInstant)) {
				return;
			}

			lastPositionStaredAt = blockPosition;
			lastPollInstant = currentInstant;
			BlockEntity blockEntity = entity.getEntityWorld().getBlockEntity(blockPosition);

			if (blockEntity instanceof LootableContainerBlockEntity || blockEntity instanceof EnderChestBlockEntity || blockEntity instanceof AbstractFurnaceBlockEntity) {
				ClientPlayNetworking.send(new InventoryRequestPayload(blockPosition));
			} else {
				CurrentContainerContext.INSTANCE.reset();
			}
		}
	}
}
