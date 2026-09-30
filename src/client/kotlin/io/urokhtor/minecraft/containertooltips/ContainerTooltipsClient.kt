package io.urokhtor.minecraft.containertooltips

import com.mojang.blaze3d.platform.InputConstants
import io.urokhtor.minecraft.containertooltips.configuration.Configuration
import io.urokhtor.minecraft.containertooltips.rendering.ContainerTooltip
import io.urokhtor.minecraft.containertooltips.rendering.EmptyContainerTooltip
import me.shedaniel.autoconfig.AutoConfig
import me.shedaniel.autoconfig.AutoConfigClient
import me.shedaniel.autoconfig.gui.registry.api.GuiRegistryAccess
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Jankson
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.JsonPrimitive
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder
import me.shedaniel.clothconfig2.api.Modifier
import me.shedaniel.clothconfig2.api.ModifierKeyCode
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.world.InteractionResult
import java.lang.reflect.Field


object ContainerTooltipsClient : ClientModInitializer {

    private val containerTooltip = ContainerTooltip()
    private val emptyContainerTooltip = EmptyContainerTooltip()
    private lateinit var configuration: Configuration
    private val tooltipIdentifier = Identifier.fromNamespaceAndPath("container-tooltips", "tooltip")
    private var screenOpen = false

    override fun onInitializeClient() {
        registerConfigurationIntegration()
        registerMessageListeners()
        registerEventListeners()
        registerRenderingHook()
    }

    private fun registerConfigurationIntegration() {
        val janksonBuilder = Jankson.builder()

        // 1. Tell Jankson how to SAVE ModifierKeyCode as a String (e.g. "key.keyboard.g")
        janksonBuilder.registerSerializer(ModifierKeyCode::class.java) { keyCode, _ ->
            JsonPrimitive(keyCode.keyCode.name)
        }

        // 2. Tell Jankson how to LOAD it back from a String
        janksonBuilder.registerDeserializer(String::class.java, ModifierKeyCode::class.java) { stringValue, _ ->
            ModifierKeyCode.of(InputConstants.getKey(stringValue), Modifier.none())
        }

        // 3. Register your config using the custom serializer factory
        AutoConfig.register(Configuration::class.java) { definition, configClass ->
            JanksonConfigSerializer(definition, configClass, janksonBuilder.build())
        }

        val builder = ConfigEntryBuilder.create()

        AutoConfigClient.getGuiRegistry(Configuration::class.java).registerTypeProvider(
            { i18n: String, field: Field, config: Any, _: Any, _: GuiRegistryAccess ->
                mutableListOf(
                    builder.startModifierKeyCodeField(
                        Component.translatable(i18n),
                        tryGetModifierKeyCode(field, config)
                    )
                        .setModifierSaveConsumer { newValue -> trySetModifierKeyCode(field, config, newValue) }.build()
                )
            },
            ModifierKeyCode::class.java
        )

        val configHolder = AutoConfig.getConfigHolder(Configuration::class.java)
        configuration = configHolder.config
        configHolder.registerSaveListener { _, config ->
            configuration = config
            InteractionResult.PASS
        }
    }

    // Helper method to safely get the field via Reflection
    private fun tryGetModifierKeyCode(field: Field, config: Any?): ModifierKeyCode? {
        try {
            return field.get(config) as ModifierKeyCode?
        } catch (exception: IllegalAccessException) {
            throw RuntimeException("Failed to access keybinding field", exception)
        }
    }

    // Helper method to safely save the field via Reflection
    private fun trySetModifierKeyCode(field: Field, config: Any?, value: ModifierKeyCode?) {
        try {
            field.set(config, value)
        } catch (exception: IllegalAccessException) {
            throw RuntimeException("Failed to save keybinding field", exception)
        }
    }

    private fun registerMessageListeners() {
        ClientPlayNetworking.registerGlobalReceiver(InventoryResponsePayload.ID) { payload, _ ->
            run {
                payload.let {
                    CurrentContainerContext.set(Container(it.name, it.items))
                }
            }
        }
    }

    private fun registerEventListeners() {
        SCREEN_OPENED.register {
            screenOpen = true
        }
        SCREEN_CLOSED.register {
            screenOpen = false
        }
    }

    private fun registerRenderingHook() {
        HudElementRegistry.addLast(tooltipIdentifier) { guiGraphics, _ ->
            CurrentContainerContext.get()?.let { container ->
                if (tooltipIsDisabled() || screenOpen) {
                    return@let
                }

                val client = Minecraft.getInstance()

                if (container.isEmpty()) {
                    emptyContainerTooltip.render(
                        client.font,
                        client.window.guiScaledWidth / 2,
                        guiGraphics,
                        container
                    )
                } else {
                    containerTooltip.render(client.font, client.window.guiScaledWidth / 2, guiGraphics, container)
                }
            }
        }
    }

    private fun tooltipIsDisabled(): Boolean =
        !configuration.showAutomatically && keyIsNotPressed(configuration.showWithKeyCode.keyCode)

    private fun keyIsNotPressed(keyCode: InputConstants.Key) =
        !InputConstants.isKeyDown(keyCode.value)
}
