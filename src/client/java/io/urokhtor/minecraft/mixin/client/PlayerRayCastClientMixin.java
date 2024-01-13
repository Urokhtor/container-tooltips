package io.urokhtor.minecraft.mixin.client;

import io.urokhtor.minecraft.CurrentInventoryContext;
import io.urokhtor.minecraft.Requests;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.time.Instant;

@Mixin(Entity.class)
public abstract class PlayerRayCastClientMixin {
	@Shadow
	public abstract World getWorld();

	@Unique
	private BlockPos lastPositionStaredAt;

	@Unique
	private Instant lastPollInstant = Instant.now();

	@Inject(at = @At("RETURN"), method = "raycast")
	private void onRayCast(CallbackInfoReturnable<HitResult> callbackInfoReturnable) {
		if (callbackInfoReturnable.getReturnValue() instanceof BlockHitResult hitResult) {
			BlockPos blockPosition = hitResult.getBlockPos();
			Instant currentInstant = Instant.now();

			// Don't send network request if we have already requested container's contents, and it has been under 1
			// since the last time we polled the contents.
			if (blockPosition.equals(lastPositionStaredAt) && currentInstant.minusSeconds(1).isBefore(lastPollInstant)) {
				return;
			}

			lastPositionStaredAt = blockPosition;
			BlockEntity blockEntity = this.getWorld().getBlockEntity(blockPosition);

			if (blockEntity instanceof LootableContainerBlockEntity || blockEntity instanceof EnderChestBlockEntity || blockEntity instanceof AbstractFurnaceBlockEntity) {
				lastPollInstant = Instant.now();
				PacketByteBuf buffer = PacketByteBufs.create();
				buffer.writeBlockPos(blockPosition);
				ClientPlayNetworking.send(Requests.INSTANCE.getINVENTORY_REQUEST(), buffer);
			} else {
				CurrentInventoryContext.INSTANCE.reset();
			}
		}
	}
}
