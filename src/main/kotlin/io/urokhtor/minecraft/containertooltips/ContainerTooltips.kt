package io.urokhtor.minecraft.containertooltips

import io.urokhtor.minecraft.containertooltips.Requests.INVENTORY_REQUEST
import io.urokhtor.minecraft.containertooltips.Requests.INVENTORY_RESPONSE
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking

object ContainerTooltips : ModInitializer {

	private val inventoryRequestHandler = InventoryRequestHandler()

	override fun onInitialize() {
		ServerPlayNetworking.registerGlobalReceiver(INVENTORY_REQUEST) { server, player, _, buffer, responseSender ->
			val blockPosition = buffer.readBlockPos()
			server.execute {
				inventoryRequestHandler.createResponse(player, blockPosition)?.let {
					val writeBuffer = PacketByteBufs.create()
					writeBuffer.writeNbt(it)
					responseSender.sendPacket(INVENTORY_RESPONSE, writeBuffer)
				}
			}
		}
	}
}
