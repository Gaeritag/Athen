package foo.starred.athen.config.ui

import foo.starred.athen.Athen
import foo.starred.athen.annotations.Priority
import foo.starred.athen.api.messaging.impl.MessagingAPI.mod
import foo.starred.athen.config.ui.pages.main.ConfigCategories
import foo.starred.athen.config.ui.pages.module.ConfigModules
import foo.starred.athen.config.ui.pages.module.elements.input.ConfigInputElement
import foo.starred.athen.config.ui.pages.module.elements.input.ConfigInputElement.Companion.configInputElement
import foo.starred.athen.hud.HUDEditor
import foo.starred.athen.modules.impl.ModSettings
import foo.starred.athen.ui.themes.Catppuccin
import foo.starred.athen.utils.command
import foo.starred.cascade.constraints.base.IPositionConstraint
import foo.starred.cascade.constraints.base.ISizeConstraint
import foo.starred.cascade.constraints.impl.data.PositionAlignment
import foo.starred.cascade.constraints.impl.data.PositionAnchor
import foo.starred.cascade.constraints.impl.position.AlignPositionConstraint
import foo.starred.cascade.constraints.impl.position.AnchorPositionConstraint
import foo.starred.cascade.constraints.impl.position.CenterPositionConstraint
import foo.starred.cascade.constraints.impl.position.FixedPositionConstraint
import foo.starred.cascade.constraints.impl.size.FixedSizeConstraint
import foo.starred.cascade.effects.impl.OutlineEffect
import foo.starred.cascade.graphics.font.CascadeFonts
import foo.starred.cascade.graphics.geometry.CascadeGeometricRadius
import foo.starred.cascade.primitives.base.impl.IPrimitiveElement
import foo.starred.cascade.primitives.impl.ContainerPrimitive.Companion.container
import foo.starred.cascade.primitives.impl.RoundedRectanglePrimitive
import foo.starred.cascade.primitives.impl.RoundedRectanglePrimitive.Companion.roundedRectangle
import foo.starred.cascade.primitives.impl.ScrollablePrimitive
import foo.starred.cascade.primitives.impl.ScrollablePrimitive.Companion.scrollable
import foo.starred.cascade.primitives.impl.TextPrimitive
import foo.starred.cascade.primitives.impl.TextPrimitive.Companion.text
import foo.starred.cascade.screen.CascadeScreen
import foo.starred.cascade.wrappers.text.impl.CascadeTextWrapper
import foo.starred.snowbird.api.center
import foo.starred.snowbird.api.client
import foo.starred.snowbird.api.lie
import foo.starred.snowbird.api.repeat
import foo.starred.snowbird.api.text.parser.impl.parse
import foo.starred.snowbird.utils.literal
import foo.starred.snowbird.utils.open
import net.minecraft.client.gui.GuiGraphicsExtractor

@Priority
object ConfigUI : CascadeScreen("Config UI [Athen]") {
    private var last = -1

    private val text = text {
        wrapper = CascadeTextWrapper
        textSize = 10f
        color = Catppuccin.Mocha.Text.argb
        position = AlignPositionConstraint(PositionAlignment.START, PositionAlignment.START, 4f, 4f)
    }

    private val tooltip = object : RoundedRectanglePrimitive() {
        override fun render(graphics: GuiGraphicsExtractor) {
            if (!visible) return

            graphics.nextStratum()
            super.render(graphics)
        }
    }.apply {
        color = Catppuccin.Mocha.Base.argb
        radius = CascadeGeometricRadius(4f)
        visible = false
        interact = false

        size = object : ISizeConstraint {
            override fun width(element: IPrimitiveElement<*>, parent: IPrimitiveElement<*>): Float = CascadeFonts.arial.width(text.text, 10f) + 8f
            override fun height(element: IPrimitiveElement<*>, parent: IPrimitiveElement<*>): Float = (CascadeFonts.arial.regular.height * 10f) + 8f
        }

        effect(OutlineEffect {
            color = Catppuccin.Mocha.Surface1.argb
        })

        adopt(text)
    }

    var headerText: TextPrimitive
        private set

    var searchBar: ConfigInputElement
        private set

    var left: RoundedRectanglePrimitive
        private set

    var right0: RoundedRectanglePrimitive
        private set

    var right: ScrollablePrimitive
        private set

    init {
        command {
            executes {
                if (!ModSettings.commandConfig) return@executes help()

                open()
                "Opened the config! <gray>(use /athen help to view commands)".mod()
            }

            "help" {
                help()
            }

            "config" {
                open()
            }

            "hud" {
                HUDEditor.open()
            }
        }

        val panel = container {
            position = CenterPositionConstraint()
            size = FixedSizeConstraint(650f, 350f)

            attach(scene)
        }

        val header = roundedRectangle {
            position = FixedPositionConstraint(0f, 0f)
            size = FixedSizeConstraint(650f, 32f)
            color = Catppuccin.Mocha.Mantle.argb
            radius = CascadeGeometricRadius(5f, 5f, 0f, 0f)

            effect(OutlineEffect {
                color = Catppuccin.Mocha.Surface0.argb
                inset = false
            })

            attach(panel)
        }

        headerText = text {
            wrapper = CascadeTextWrapper
            text = "<bold><#FDCCDA>A<#FCDDD3>t<#FAEDCB>h<#F0E2D7>e<#E5D8E4>n<#DBCDF0>".parse()
            textSize = 16f
            color = Catppuccin.Mocha.Text.argb
            position = AlignPositionConstraint(PositionAlignment.START, PositionAlignment.CENTER, 12f, 0f)

            attach(header)
        }

        searchBar = configInputElement {
            position = AlignPositionConstraint(PositionAlignment.END, PositionAlignment.CENTER, -12f, 0f)
            size = FixedSizeConstraint(120f, 18f)
            placeholder = "Search..."

            attach(header)
            update {
                ConfigModules.active = null
                ConfigModules.fn()
            }
        }

        left = roundedRectangle {
            position = AnchorPositionConstraint({ header }, PositionAnchor.BELOW)
            size = FixedSizeConstraint(140f, 318f)
            color = Catppuccin.Mocha.Mantle.argb
            radius = CascadeGeometricRadius(0f, 0f, 5f, 0f)

            effect(OutlineEffect {
                color = Catppuccin.Mocha.Surface0.argb
                inset = false
            })

            attach(panel)
        }

        right0 = roundedRectangle {
            position = AnchorPositionConstraint({ left }, PositionAnchor.RIGHT)
            size = FixedSizeConstraint(510f, 318f)
            color = Catppuccin.Mocha.Crust.argb
            radius = CascadeGeometricRadius(0f, 0f, 0f, 5f)

            effect(OutlineEffect {
                color = Catppuccin.Mocha.Surface0.argb
                inset = false
            })

            attach(panel)
        }

        right = scrollable {
            position = FixedPositionConstraint(0f, 0f)
            size = FixedSizeConstraint(510f, 318f)
            attach(right0)
        }

        ConfigCategories.fn()
        ConfigModules.fn()

        tooltip.attach(scene)
    }

    fun show(text0: String, x: Double, y: Double) {
        text.text = text0.parse()
        tooltip.visible = true
        tooltip.position = object : IPositionConstraint {
            override fun x(element: IPrimitiveElement<*>, parent: IPrimitiveElement<*>): Float = x.toFloat() + 5f
            override fun y(element: IPrimitiveElement<*>, parent: IPrimitiveElement<*>): Float = y.toFloat() + 5f
        }
    }

    fun hide() {
        tooltip.visible = false
    }

    override fun init() {
        super.init()
        if (last != -1) return
        last = client.options.guiScale().get()
        client.options.guiScale().set(2)
    }

    override fun onClose() {
        if (ConfigModules.active != null) {
            ConfigModules.active = null
            ConfigModules.fn()
            return
        }

        super.onClose()
        hide()
        if (last == -1) return
        client.options.guiScale().set(last)
        last = -1
    }

    private fun help() {
        val divider = ("§8§m" + ("-".repeat())).literal()

        divider.lie()
        "§bAthen Commands".center().lie()
        divider.lie()

        val commands = listOf(
            "/${Athen.modId} config" to "Open the configuration menu",
            "/${Athen.modId} hud" to "Open the HUD editor",
            "/${Athen.modId} simulate terminals" to "Terminal simulator",
            "/${Athen.modId} radial help" to "Info about radial menu",
            "/${Athen.modId} visuals help" to "Info about visual words replacement",
            "/${Athen.modId} carry help" to "Info about slayer carry commands",
            "/${Athen.modId} dcarry help" to "Info about dungeon carry commands",
            "/${Athen.modId} kcarry help" to "Info about kuudra carry commands",
            "/${Athen.modId} clear chat" to "Clear the chat history",
            "/${Athen.modId} stats <name>" to "View stats for any player",
            "/${Athen.modId} times slayers" to "Shows the slayer kill times",
            "/${Athen.modId} times kuudra <tier>" to "Shows the kuudra pbs",
            "/${Athen.modId} toggle feature <featureKey>" to "Toggles the specified feature!",
            "/${Athen.modId} irc help" to "View all IRC commands"
        )

        for ((c, d) in commands) "  <${Catppuccin.Mocha.Green.argb}>$c <dark_gray>- <gray>$d".parse().lie()

        divider.lie()
    }
}
