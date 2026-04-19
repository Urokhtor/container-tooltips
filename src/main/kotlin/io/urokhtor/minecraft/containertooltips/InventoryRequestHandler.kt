package io.urokhtor.minecraft.containertooltips

import net.minecraft.core.BlockPos
import net.minecraft.core.NonNullList
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.ChestBlock
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.ChestBlockEntity
import net.minecraft.world.level.block.entity.EnderChestBlockEntity
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity

class InventoryRequestHandler {

    fun createResponse(
        player: ServerPlayer,
        blockPosition: BlockPos
    ): InventoryResponsePayload? {
        return when (val blockEntity: BlockEntity? = player.level().getBlockEntity(blockPosition)) {
            is ChestBlockEntity -> readChestInventory(blockEntity, player)
            is EnderChestBlockEntity -> readEnderChestInventory(player)
            is RandomizableContainerBlockEntity -> readGenericInventory(blockEntity)
            is AbstractFurnaceBlockEntity -> blockEntity.readInventory()
            else -> null
        }
    }

    /**
     * Reads contents of a chest container. Double chests are supported by using [ChestBlock.getContainer]. They are
     * also the reason why [readGenericInventory] does not work for chests, because each side of a double chest has a
     * separate inventory. Without this, the client would get only one half of double chest's inventory.
     */
    private fun readChestInventory(
        blockEntity: ChestBlockEntity,
        player: ServerPlayer
    ): InventoryResponsePayload? {
        if (blockEntity.hasNotBeenLooted()) {
            return null
        }

        val blockState = blockEntity.blockState
        val inventory = ChestBlock.getContainer(
            blockState.block as ChestBlock,
            blockState,
            player.level(),
            blockEntity.blockPos,
            true
        )

        val items = NonNullList.withSize(inventory!!.count(), ItemStack.EMPTY)
        IntRange(0, inventory.count() - 1).forEach { slot ->
            items[slot] = inventory.getItem(slot)
        }

        return InventoryResponsePayload(
            name = blockEntity.displayName.getString(32),
            maxSize = inventory.count(),
            items = items
        )
    }

    private fun readEnderChestInventory(player: ServerPlayer) = InventoryResponsePayload(
        name = Component.translatable("container.enderchest").getString(32),
        maxSize = player.enderChestInventory.count(),
        items = player.enderChestInventory.items
    )

    private fun readGenericInventory(blockEntity: RandomizableContainerBlockEntity): InventoryResponsePayload? {
        if (blockEntity.hasNotBeenLooted()) {
            return null
        }

        return blockEntity.readInventory()
    }
}

fun RandomizableContainerBlockEntity.hasNotBeenLooted() = this.lootTable != null

private fun BaseContainerBlockEntity.readInventory(): InventoryResponsePayload {
    val defaultedList = NonNullList.withSize(this.count(), ItemStack.EMPTY)
    IntRange(0, this.count() - 1).forEach { slot ->
        defaultedList[slot] = this.getItem(slot)
    }

    return InventoryResponsePayload(
        name = this.displayName.getString(32),
        maxSize = this.count(),
        items = defaultedList
    )
}
