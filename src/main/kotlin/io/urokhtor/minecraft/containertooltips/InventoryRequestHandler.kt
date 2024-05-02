package io.urokhtor.minecraft.containertooltips

import net.minecraft.block.ChestBlock
import net.minecraft.block.entity.AbstractFurnaceBlockEntity
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.ChestBlockEntity
import net.minecraft.block.entity.EnderChestBlockEntity
import net.minecraft.block.entity.LootableContainerBlockEntity
import net.minecraft.inventory.Inventories
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtInt
import net.minecraft.nbt.NbtString
import net.minecraft.registry.RegistryWrapper
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos

class InventoryRequestHandler {

    fun createResponse(
        player: ServerPlayerEntity,
        registryWrapperLookup: RegistryWrapper.WrapperLookup,
        blockPosition: BlockPos
    ): NbtCompound? {
        return when (val blockEntity: BlockEntity? = player.serverWorld.getBlockEntity(blockPosition)) {
            is ChestBlockEntity -> readChestInventory(blockEntity, player, registryWrapperLookup)
            is LootableContainerBlockEntity -> readGenericInventory(blockEntity, registryWrapperLookup)
            is EnderChestBlockEntity -> readEnderChestInventory(player, registryWrapperLookup)
            is AbstractFurnaceBlockEntity -> readFurnaceInventory(blockEntity, registryWrapperLookup)
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
        player: ServerPlayerEntity,
        registryWrapperLookup: RegistryWrapper.WrapperLookup
    ): NbtCompound {
        val blockState = blockEntity.cachedState
        val inventory = ChestBlock.getInventory(
            blockState.block as ChestBlock,
            blockState,
            player.serverWorld,
            blockEntity.pos,
            true
        )

        val defaultedList = DefaultedList.ofSize(inventory!!.size(), ItemStack.EMPTY)
        IntRange(0, inventory.size() - 1).map { slot ->
            defaultedList.set(slot, inventory.getStack(slot))
        }

        val nbtCompound = NbtCompound()
        nbtCompound.put(Responses.MAX_SIZE, NbtInt.of(inventory.size()))
        Inventories.writeNbt(nbtCompound, defaultedList, registryWrapperLookup)
        nbtCompound.put(Responses.NAME, NbtString.of(blockEntity.displayName.asTruncatedString(32)))
        return nbtCompound
    }

    private fun readGenericInventory(
        blockEntity: LootableContainerBlockEntity,
        registryWrapperLookup: RegistryWrapper.WrapperLookup
    ): NbtCompound {
        val nbtCompound = blockEntity.createNbt(registryWrapperLookup)
        nbtCompound.put(Responses.MAX_SIZE, NbtInt.of(blockEntity.size()))
        nbtCompound.put(Responses.NAME, NbtString.of(blockEntity.displayName.asTruncatedString(32)))
        return nbtCompound
    }

    private fun readEnderChestInventory(
        player: ServerPlayerEntity,
        registryWrapperLookup: RegistryWrapper.WrapperLookup
    ): NbtCompound {
        val nbtList = player.enderChestInventory.toNbtList(registryWrapperLookup)
        val nbtCompound = NbtCompound()
        nbtCompound.put(Responses.ITEMS, nbtList)
        nbtCompound.put(Responses.MAX_SIZE, NbtInt.of(player.enderChestInventory.size()))
        nbtCompound.put(Responses.NAME, NbtString.of(Text.translatable("container.enderchest").asTruncatedString(32)))
        return nbtCompound
    }

    private fun readFurnaceInventory(
        blockEntity: AbstractFurnaceBlockEntity,
        registryWrapperLookup: RegistryWrapper.WrapperLookup
    ): NbtCompound {
        val nbtCompound = blockEntity.createNbt(registryWrapperLookup)
        nbtCompound.put(Responses.MAX_SIZE, NbtInt.of(blockEntity.size()))
        nbtCompound.put(Responses.NAME, NbtString.of(blockEntity.displayName.asTruncatedString(32)))
        return nbtCompound
    }
}
