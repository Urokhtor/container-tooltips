package io.urokhtor.minecraft

import io.urokhtor.minecraft.Requests.INVENTORY_REQUEST
import io.urokhtor.minecraft.Requests.INVENTORY_RESPONSE
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking

object ContainerTooltips : ModInitializer {

	private val inventoryRequestHandler = InventoryRequestHandler()

	override fun onInitialize() {
		ServerPlayNetworking.registerGlobalReceiver(INVENTORY_REQUEST) { server, player, _, buffer, responseSender ->
			server.execute {
				inventoryRequestHandler.createResponse(player, buffer.readBlockPos())?.let {
					val writeBuffer = PacketByteBufs.create()
					writeBuffer.writeNbt(it)
					responseSender.sendPacket(INVENTORY_RESPONSE, writeBuffer)
				}
			}
		}
	}
}
