package io.urokhtor.minecraft.containertooltips

import net.minecraft.block.ChestBlock
import net.minecraft.block.entity.AbstractFurnaceBlockEntity
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.ChestBlockEntity
import net.minecraft.block.entity.EnderChestBlockEntity
import net.minecraft.block.entity.LootableContainerBlockEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos

class InventoryRequestHandler {

    fun createResponse(
        player: ServerPlayerEntity,
        blockPosition: BlockPos
    ): InventoryResponsePayload? {
        return when (val blockEntity: BlockEntity? = player.entityWorld.getBlockEntity(blockPosition)) {
            is ChestBlockEntity -> readChestInventory(blockEntity, player)
            is EnderChestBlockEntity -> readEnderChestInventory(player)
            is LootableContainerBlockEntity -> readGenericInventory(blockEntity)
            is AbstractFurnaceBlockEntity -> readFurnaceInventory(blockEntity)
            else -> null
        }
    }

    /**
     * Reads contents of a chest container. Double chests are supported by using [ChestBlock.getInventory]. They are
     * also the reason why [readGenericInventory] does not work for chests, because each side of a double chest has a
     * separate inventory. Without this, the client would get only one half of double chest's inventory.
     */
    private fun readChestInventory(
        blockEntity: ChestBlockEntity,
        player: ServerPlayerEntity
    ): InventoryResponsePayload {
        val blockState = blockEntity.cachedState
        val inventory = ChestBlock.getInventory(
            blockState.block as ChestBlock,
            blockState,
            player.entityWorld,
            blockEntity.pos,
            true
        )

        val defaultedList = DefaultedList.ofSize(inventory!!.size(), ItemStack.EMPTY)
        IntRange(0, inventory.size() - 1).forEach { slot ->
            defaultedList[slot] = inventory.getStack(slot)
        }

        return InventoryResponsePayload(
            name = blockEntity.displayName.asTruncatedString(32),
            maxSize = inventory.size(),
            items = defaultedList
        )
    }

    private fun readEnderChestInventory(player: ServerPlayerEntity) = InventoryResponsePayload(
        name = Text.translatable("container.enderchest").asTruncatedString(32),
        maxSize = player.enderChestInventory.size(),
        items = player.enderChestInventory.heldStacks
    )

    private fun readGenericInventory(blockEntity: LootableContainerBlockEntity): InventoryResponsePayload {
        val defaultedList = DefaultedList.ofSize(blockEntity.size(), ItemStack.EMPTY)
        IntRange(0, blockEntity.size() - 1).forEach { slot ->
            defaultedList[slot] = blockEntity.getStack(slot)
        }

        return InventoryResponsePayload(
            name = blockEntity.displayName.asTruncatedString(32),
            maxSize = blockEntity.size(),
            items = defaultedList
        )
    }

    private fun readFurnaceInventory(blockEntity: AbstractFurnaceBlockEntity): InventoryResponsePayload {
        val defaultedList = DefaultedList.ofSize(blockEntity.size(), ItemStack.EMPTY)
        IntRange(0, blockEntity.size() - 1).forEach { slot ->
            defaultedList[slot] = blockEntity.getStack(slot)
        }

        return InventoryResponsePayload(
            name = blockEntity.displayName.asTruncatedString(32),
            maxSize = blockEntity.size(),
            items = defaultedList
        )
    }
}
