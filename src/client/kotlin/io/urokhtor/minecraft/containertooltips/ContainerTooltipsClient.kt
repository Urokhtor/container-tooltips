package io.urokhtor.minecraft.containertooltips

import io.urokhtor.minecraft.containertooltips.Requests.INVENTORY_RESPONSE
import io.urokhtor.minecraft.containertooltips.configuration.Configuration
import io.urokhtor.minecraft.containertooltips.rendering.ContainerTooltip
import io.urokhtor.minecraft.containertooltips.rendering.EmptyContainerTooltip
import me.shedaniel.autoconfig.AutoConfig
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.InputUtil
import net.minecraft.item.AirBlockItem
import net.minecraft.util.ActionResult

object ContainerTooltipsClient : ClientModInitializer {

	private val inventoryResponseHandler = InventoryResponseHandler()
	private val containerTooltip = ContainerTooltip()
	private val emptyContainerTooltip = EmptyContainerTooltip()
	private lateinit var configuration: Configuration

	override fun onInitializeClient() {
		AutoConfig.register(Configuration::class.java, ::JanksonConfigSerializer)

		val configHolder = AutoConfig.getConfigHolder(Configuration::class.java)
		configuration = configHolder.config
		configHolder.registerSaveListener { _, config ->
			configuration = config
			ActionResult.PASS
		}

		ClientPlayNetworking.registerGlobalReceiver(INVENTORY_RESPONSE) { _, _, buffer, _ ->
			run {
				buffer.readNbt()?.let {
					CurrentContainerContext.set(inventoryResponseHandler.parseAsContainer(it))
				}
			}
		}

		HudRenderCallback.EVENT.register { guiGraphics, _ ->
			CurrentContainerContext.get()?.let {
				if (!configuration.showAutomatically && keyIsNotPressed(configuration.showWithKeyCode)) {
					return@let
				}

				val client = MinecraftClient.getInstance()

				val isInventoryEmpty = it.inventory
					.none { inventory -> inventory.item !is AirBlockItem }

				if (isInventoryEmpty) {
					emptyContainerTooltip.render(client.textRenderer, client.window.scaledWidth / 2, guiGraphics, it)
				} else {
					containerTooltip.render(client.textRenderer, client.window.scaledWidth / 2, guiGraphics, it)
				}
			}
		}
	}

	private fun keyIsNotPressed(keyCode: Int) =
		!InputUtil.isKeyPressed(MinecraftClient.getInstance().window.handle, keyCode)
}
