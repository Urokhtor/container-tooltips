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
                readChestInventory(player, blockPosition)
            } else {
                readGenericInventory(blockEntity)
            }
        } else if (blockEntity is EnderChestBlockEntity) {
            readEnderChestInventory(player)
        } else {
            null
        }
    }

    /**
     * Reads contents of a chest container. Double chests are supported by using [ChestBlock.getInventory]. They are
     * also the reason why [readGenericInventory] does not work for chests, because each side of a double chest has a
     * separate inventory. Without this, the client would get only one half of double chest's inventory.
     */
    private fun readChestInventory(
        player: ServerPlayerEntity,
        blockPosition: BlockPos
    ): NbtCompound {
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
        return nbtCompound
    }

    private fun readGenericInventory(blockEntity: LootableContainerBlockEntity): NbtCompound? {
        val nbtCompound = blockEntity.createNbt()
        nbtCompound.put("MaxSize", NbtInt.of(blockEntity.size()))
        return nbtCompound
    }

    private fun readEnderChestInventory(player: ServerPlayerEntity): NbtCompound {
        val nbtList = player.enderChestInventory.toNbtList()
        val nbtCompound = NbtCompound()
        nbtCompound.put("Items", nbtList)
        nbtCompound.put("MaxSize", NbtInt.of(player.enderChestInventory.size()))
        return nbtCompound
    }
}
