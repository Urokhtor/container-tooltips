package io.urokhtor.minecraft.containertooltips

import io.netty.buffer.ByteBuf
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.packet.CustomPayload
import net.minecraft.network.packet.CustomPayload.Id

data class InventoryResponsePayload(val buffer: NbtCompound) : CustomPayload {
    override fun getId() = ID

    companion object {
        val ID: Id<InventoryResponsePayload> = CustomPayload.id("container-tooltips:inventory-response")
        val PACKET_CODEC: PacketCodec<ByteBuf, InventoryResponsePayload> = object:
            PacketCodec<ByteBuf, InventoryResponsePayload> {
            override fun decode(buf: ByteBuf): InventoryResponsePayload {
                return InventoryResponsePayload(PacketByteBuf.readNbt(buf)!!)
            }

            override fun encode(buf: ByteBuf, value: InventoryResponsePayload) {
                PacketByteBuf.writeNbt(buf, value.buffer)
            }

        }
    }
}
