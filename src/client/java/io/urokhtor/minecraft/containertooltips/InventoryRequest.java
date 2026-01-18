package io.urokhtor.minecraft.containertooltips;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.time.Instant;

public class InventoryRequest {

    public record StaringContext(BlockPos lastPositionStaredAt, Instant lastPollInstant) { }

    public static StaringContext handleInventoryRequest(BlockHitResult hitResult, StaringContext staringContext, World world) {
        BlockPos blockPosition = hitResult.getBlockPos();
        Instant currentInstant = Instant.now();

        if (staringContext == null) {
            staringContext = new StaringContext(blockPosition, currentInstant);
        }

        // Don't send network request if we have already requested container's contents, and it has been under 1
        // since the last time we polled the contents.
        if (blockPosition.equals(staringContext.lastPositionStaredAt()) && currentInstant.minusSeconds(1).isBefore(staringContext.lastPollInstant())) {
            return staringContext;
        }

        BlockEntity blockEntity = world.getBlockEntity(blockPosition);

        if (blockEntity instanceof LootableContainerBlockEntity || blockEntity instanceof EnderChestBlockEntity || blockEntity instanceof AbstractFurnaceBlockEntity) {
            ClientPlayNetworking.send(new InventoryRequestPayload(blockPosition));
        } else {
            CurrentContainerContext.INSTANCE.reset();
        }

        return new StaringContext(blockPosition, currentInstant);
    }
}
