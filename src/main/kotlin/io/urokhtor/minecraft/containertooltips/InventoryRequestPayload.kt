package io.urokhtor.minecraft.containertooltips

import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.packet.CustomPayload
import net.minecraft.network.packet.CustomPayload.Id
import net.minecraft.util.math.BlockPos

data class InventoryRequestPayload(val blockPos: BlockPos) : CustomPayload {
    override fun getId() = ID

    companion object {
        val ID: Id<InventoryRequestPayload> = CustomPayload.id("container-tooltips/inventory-request")
        val PACKET_CODEC: PacketCodec<ByteBuf, InventoryRequestPayload> = object:
            PacketCodec<ByteBuf, InventoryRequestPayload> {
            override fun decode(buf: ByteBuf): InventoryRequestPayload {
                return InventoryRequestPayload(BlockPos.PACKET_CODEC.decode(buf))
            }

            override fun encode(buf: ByteBuf, value: InventoryRequestPayload) {
                BlockPos.PACKET_CODEC.encode(buf, value.blockPos)
            }
        }
    }
}
