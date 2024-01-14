package io.urokhtor.minecraft

import io.urokhtor.minecraft.Requests.INVENTORY_RESPONSE
import io.urokhtor.minecraft.configuration.Configuration
import me.shedaniel.autoconfig.AutoConfig
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.InputUtil
import net.minecraft.util.ActionResult
import org.lwjgl.glfw.GLFW

object ContainerTooltipsClient : ClientModInitializer {

	private val inventoryResponseHandler = InventoryResponseHandler()
	private val inventoryTooltip = InventoryTooltip()
	private lateinit var configuration: Configuration
	private val previewKey = InputUtil.Type.KEYSYM.createFromCode(GLFW.GLFW_KEY_LEFT_SHIFT)

	override fun onInitializeClient() {
		AutoConfig.register(Configuration::class.java, ::JanksonConfigSerializer)

		val configHolder = AutoConfig.getConfigHolder(Configuration::class.java)
		configuration = configHolder.config

		ClientPlayNetworking.registerGlobalReceiver(INVENTORY_RESPONSE) { _, _, buffer, _ ->
			run {
				buffer.readNbt()?.let {
					CurrentInventoryContext.set(inventoryResponseHandler.parseResponse(it))
				}
			}
		}

		HudRenderCallback.EVENT.register { guiGraphics, _ ->
			CurrentInventoryContext.get()?.let {
				if (!configuration.showAutomatically &&
					!InputUtil.isKeyPressed(MinecraftClient.getInstance().window.handle, previewKey.code)) {
					return@let
				}

				val client = MinecraftClient.getInstance()
				inventoryTooltip.render(client.textRenderer, client.window.scaledWidth / 2, guiGraphics, it)
			}
		}
	}
}
