package foo.starred.athen.events.dispatcher

import foo.starred.athen.annotations.Priority
import foo.starred.athen.events.*
import foo.starred.snowbird.api.client
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents
import net.fabricmc.fabric.api.event.player.*
import net.minecraft.world.InteractionResult

@Priority
object FabricEventDispatcher {
    init {
        ClientLifecycleEvents.CLIENT_STARTED.register { _ ->
            GameEvent.Start.post()
        }

        ClientLifecycleEvents.CLIENT_STOPPING.register { _ ->
            GameEvent.Stop.post()
        }

        ClientPlayConnectionEvents.JOIN.register { _, _, _ ->
            LocationEvent.Server.Connect.post()
        }

        ClientPlayConnectionEvents.DISCONNECT.register { _, _ ->
            LocationEvent.Server.Disconnect.post()
        }

        ClientEntityEvents.ENTITY_LOAD.register { entity, _ ->
            EntityEvent.Load(entity).post()
        }

        ClientEntityEvents.ENTITY_UNLOAD.register { entity, _ ->
            EntityEvent.Unload(entity).post()
        }

        ClientTickEvents.START_CLIENT_TICK.register { _ ->
            TickEvent.Client.Start.post()
        }

        ClientTickEvents.END_CLIENT_TICK.register { _ ->
            TickEvent.Client.End.post()
            //~ if >= 26.2 'client.isSingleplayer' -> 'client.singleplayerServer != null'
            if (client.isSingleplayer) TickEvent.Server.post()
        }

        //~ if >= 26.2 'AFTER_TRANSLUCENT_TERRAIN' -> 'COLLECT_SUBMITS'
        LevelRenderEvents.AFTER_TRANSLUCENT_TERRAIN.register { context ->
            WorldRenderEvent.Extract.post()
            //~ if >= 26.2 'bufferSource' -> 'submitNodeCollector'
            WorldRenderEvent.Render(context.poseStack(), context.bufferSource()).post()
        }

        ClientReceiveMessageEvents.ALLOW_GAME.register { component, bool ->
            if (bool) return@register true
            !MessageEvent.Chat.Intercept(component).post()
        }

        ScreenEvents.BEFORE_INIT.register { _, screen, _, _ ->
            ScreenMouseEvents.allowMouseClick(screen).register { _, event ->
                !GuiEvent.Input.Mouse.Press(event).post()
            }

            ScreenMouseEvents.allowMouseRelease(screen).register { _, event ->
                !GuiEvent.Input.Mouse.Release(event).post()
            }

            ScreenMouseEvents.allowMouseScroll(screen).register { _, _, _, _, amount ->
                !GuiEvent.Input.Mouse.Scroll(amount).post()
            }

            ScreenKeyboardEvents.allowKeyPress(screen).register { _, event ->
                !GuiEvent.Input.Key.Press(event).post()
            }

            ScreenKeyboardEvents.allowKeyRelease(screen).register { _, event ->
                !GuiEvent.Input.Key.Release(event).post()
            }
        }

        UseItemCallback.EVENT.register { _, _, _ ->
            val a = PlayerEvent.Interact.None.post()
            val b = PlayerEvent.Interact.Any.post()

            if (!a && !b) return@register InteractionResult.PASS
            InteractionResult.FAIL
        }

        UseBlockCallback.EVENT.register { _, _, _, a ->
            val a = PlayerEvent.Interact.Block(a.blockPos).post()
            val b = PlayerEvent.Interact.Any.post()

            if (!a && !b) return@register InteractionResult.PASS
            InteractionResult.FAIL
        }

        UseEntityCallback.EVENT.register { _, _, _, entity, _ ->
            val a = PlayerEvent.Interact.Entity(entity).post()
            val b = PlayerEvent.Interact.Any.post()

            if (!a && !b) return@register InteractionResult.PASS
            InteractionResult.FAIL
        }

        AttackBlockCallback.EVENT.register { _, _, _, pos, _ ->
            val a = PlayerEvent.Attack.Block(pos).post()
            val b = PlayerEvent.Attack.Any.post()

            if (!a && !b) return@register InteractionResult.PASS
            InteractionResult.FAIL
        }

        AttackEntityCallback.EVENT.register { _, _, _, entity, _ ->
            val a = PlayerEvent.Attack.Entity(entity).post()
            val b = PlayerEvent.Attack.Any.post()

            if (!a && !b) return@register InteractionResult.PASS
            InteractionResult.FAIL
        }
    }
}