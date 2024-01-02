package io.urokhtor.minecraft

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.util.Identifier

object Containertooltips : ModInitializer {

	private val inventoryRequestHandler = InventoryRequestHandler()
	private val INVENTORY_REQUEST: Identifier = Identifier("chest-tooltips", "inventory-request")
	private val INVENTORY_RESPONSE: Identifier = Identifier("chest-tooltips", "inventory-response")

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
