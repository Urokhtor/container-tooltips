package io.urokhtor.minecraft.containertooltips

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.block.entity.AbstractFurnaceBlockEntity
import net.minecraft.block.entity.EnderChestBlockEntity
import net.minecraft.block.entity.LootableContainerBlockEntity
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.time.Instant

object InventoryRequest {
    fun handleInventoryRequest(
        hitResult: BlockHitResult,
        staringContext: StaringContext?,
        world: World
    ): StaringContext {
        var staringContext = staringContext
        val blockPosition = hitResult.blockPos
        val currentInstant = Instant.now()

        if (staringContext == null) {
            staringContext = StaringContext(blockPosition, currentInstant)
        }

        // Don't send network request if we have already requested container's contents, and it has been under 1
        // since the last time we polled the contents.
        if (blockPosition == staringContext.lastPositionStaredAt && currentInstant.minusSeconds(1)
                .isBefore(staringContext.lastPollInstant)
        ) {
            return staringContext
        }

        val blockEntity = world.getBlockEntity(blockPosition)

        if (blockEntity is LootableContainerBlockEntity || blockEntity is EnderChestBlockEntity || blockEntity is AbstractFurnaceBlockEntity) {
            ClientPlayNetworking.send(InventoryRequestPayload(blockPosition))
        } else {
            CurrentContainerContext.reset()
        }

        return StaringContext(blockPosition, currentInstant)
    }

    @JvmRecord
    data class StaringContext(val lastPositionStaredAt: BlockPos?, val lastPollInstant: Instant?)
}
