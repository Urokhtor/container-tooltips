package io.urokhtor.minecraft

import net.minecraft.block.ChestBlock
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.ChestBlockEntity
import net.minecraft.block.entity.EnderChestBlockEntity
import net.minecraft.block.entity.LootableContainerBlockEntity
import net.minecraft.inventory.Inventories
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtInt
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos

class InventoryRequestHandler {

    fun createResponse(player: ServerPlayerEntity, blockPosition: BlockPos): NbtCompound? {
        val blockEntity: BlockEntity? = player.serverWorld.getBlockEntity(blockPosition)

        return if (blockEntity != null && blockEntity is LootableContainerBlockEntity) {
            if (blockEntity is ChestBlockEntity) {
                val blockState = player.serverWorld.getBlockState(blockPosition)
                val inventory = ChestBlock.getInventory(
                    blockState.block as ChestBlock,
                    blockState,
                    player.serverWorld,
                    blockPosition,
                    true
                )

                val defaultedList = DefaultedList.ofSize(inventory!!.size(), ItemStack.EMPTY);
                IntRange(0, inventory.size() - 1).map { slot ->
                    defaultedList.set(slot, inventory.getStack(slot))
                }

                val nbtCompound = NbtCompound()
                nbtCompound.put("MaxSize", NbtInt.of(inventory.size()))
                Inventories.writeNbt(nbtCompound, defaultedList)
                nbtCompound
            } else {
                val nbtCompound = blockEntity.createNbt()
                nbtCompound.put("MaxSize", NbtInt.of(blockEntity.size()))
                nbtCompound
            }
        } else if (blockEntity is EnderChestBlockEntity) {
            val nbtList = player.enderChestInventory.toNbtList()
            val nbtCompound = NbtCompound()
            nbtCompound.put("Items", nbtList)
            nbtCompound.put("MaxSize", NbtInt.of(player.enderChestInventory.size()))
            nbtCompound
        } else {
            null
        }
    }
}
