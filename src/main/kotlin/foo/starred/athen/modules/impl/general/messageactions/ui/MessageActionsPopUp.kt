@file:Suppress("PrivatePropertyName")

package foo.starred.athen.modules.impl.general.messageactions.ui

import foo.starred.athen.api.rendering.ui.components.impl.MultiCheckboxComponent
import foo.starred.athen.api.rendering.ui.components.impl.MultiCheckboxComponent.Companion.multiCheckbox
import foo.starred.athen.api.rendering.ui.components.impl.TextFieldComponent
import foo.starred.athen.api.rendering.ui.components.impl.TextFieldComponent.Companion.textField
import foo.starred.athen.modules.impl.general.messageactions.MessageActions
import foo.starred.athen.modules.impl.general.messageactions.actions.IMessageAction
import foo.starred.athen.modules.impl.general.messageactions.data.ActionEntry
import foo.starred.athen.modules.impl.general.messageactions.data.MatchType
import foo.starred.athen.ui.themes.Catppuccin.Mocha
import foo.starred.cascade.constraints.impl.data.PositionAnchor
import foo.starred.cascade.constraints.impl.position.AnchorPositionConstraint
import foo.starred.cascade.constraints.impl.position.CenterPositionConstraint
import foo.starred.cascade.constraints.impl.position.FixedPositionConstraint
import foo.starred.cascade.constraints.impl.position.MixedPositionConstraint
import foo.starred.cascade.constraints.impl.size.FillSizeConstraint
import foo.starred.cascade.constraints.impl.size.FixedSizeConstraint
import foo.starred.cascade.constraints.impl.size.MixedSizeConstraint
import foo.starred.cascade.constraints.impl.size.PercentSizeConstraint
import foo.starred.cascade.effects.impl.OutlineEffect
import foo.starred.cascade.events.impl.KeyEvent
import foo.starred.cascade.events.impl.MouseEvent
import foo.starred.cascade.primitives.impl.ContainerPrimitive
import foo.starred.cascade.primitives.impl.RectanglePrimitive
import foo.starred.cascade.primitives.impl.RectanglePrimitive.Companion.rectangle
import foo.starred.cascade.primitives.impl.TextPrimitive
import foo.starred.cascade.primitives.impl.TextPrimitive.Companion.text
import foo.starred.cascade.screen.CascadeScreen
import foo.starred.snowbird.utils.literal

class MessageActionsPopUp(
    private val gui: CascadeScreen,
    private val onClose: () -> Unit
) : ContainerPrimitive() {
    private var entry: ActionEntryData? = null
    private var action = 0
    private var cancel = false
    private var category = ""
    private var match = MatchType.CONTAINS
    private var categories: List<String> = emptyList()

    private var title: TextPrimitive = TextPrimitive.NONE
    private var pattern: TextFieldComponent
    private var value: TextFieldComponent
    private var delay: TextFieldComponent
    private var `checkbox$match`: MultiCheckboxComponent
    private var `checkbox$category`: MultiCheckboxComponent
    private var `value$label`: TextPrimitive = TextPrimitive.NONE
    private var `value$disabled`: RectanglePrimitive
    private var `cancel$box`: RectanglePrimitive
    private lateinit var `cancel$box$outline`: OutlineEffect
    private lateinit var `cancel$inner`: RectanglePrimitive

    data class ActionEntryData(val index: Int, val entry: ActionEntry)
    data class ActionButton(val rect: RectanglePrimitive, val text: TextPrimitive, val outline: OutlineEffect, val id: Int)

    private val actions = mutableListOf<ActionButton>()

    init {
        size = FillSizeConstraint()
        position = FixedPositionConstraint(0, 0)

        on<MouseEvent.Press> {
            if (root.focused is MultiCheckboxComponent) root.focused = null
            cancel()
        }

        rectangle {
            size = FillSizeConstraint()
            position = FixedPositionConstraint(0, 0)
            color = Mocha.Crust.withAlpha(0.6f)

            on<MouseEvent.Press> {
                if (root.focused is MultiCheckboxComponent) root.focused = null
                cancel()
            }

            attach(this@MessageActionsPopUp)
        }

        val box = rectangle {
            size = FixedSizeConstraint(380, 260)
            position = CenterPositionConstraint()
            color = Mocha.Base.argb

            effect(OutlineEffect {
                color = Mocha.Surface0.argb
            })

            on<MouseEvent.Press> {
                if (root.focused is MultiCheckboxComponent) root.focused = null
                cancel()
            }

            attach(this@MessageActionsPopUp)
        }

        val header = container {
            position = FixedPositionConstraint(0, 0)
            size = MixedSizeConstraint(PercentSizeConstraint(100f, 0f), FixedSizeConstraint(0, 24))
            attach(box)

            adopt(text {
                text = "Create Action".literal()
                color = Mocha.Lavender.argb
                position = MixedPositionConstraint(FixedPositionConstraint(8, 0), CenterPositionConstraint())
            }.also { title = it })
        }

        val divider = rectangle {
            size = MixedSizeConstraint(PercentSizeConstraint(100f, 0f), FixedSizeConstraint(0, 1))
            position = AnchorPositionConstraint({ header }, PositionAnchor.BELOW)
            color = Mocha.Surface0.argb
            attach(box)
        }

        val pattern0 = text {
            text = "Pattern".literal()
            color = Mocha.Subtext0.argb
            position = AnchorPositionConstraint({ divider }, PositionAnchor.BELOW, 16, 8)
            attach(box)
        }

        text {
            text = "Match Type".literal()
            color = Mocha.Subtext0.argb
            position = AnchorPositionConstraint({ divider }, PositionAnchor.BELOW, 200, 8)
            attach(box)
        }

        pattern = textField {
            size = FixedSizeConstraint(170, 16)
            position = AnchorPositionConstraint({ pattern0 }, PositionAnchor.BELOW, 0, 2)
            placeholder = "Pattern to match"
            attach(box)
        }

        `checkbox$match` = multiCheckbox {
            size = FixedSizeConstraint(170, 16)
            position = AnchorPositionConstraint({ pattern0 }, PositionAnchor.BELOW, 184, 2)
            items = MatchType.entries.map { it.displayName }

            selected {
                match == MatchType.entries[it]
            }

            select {
                match = MatchType.entries[it]
                `checkbox$match`.text = match.displayName
            }

            attach(box)
        }

        val action0 = text {
            text = "Action".literal()
            color = Mocha.Subtext0.argb
            position = AnchorPositionConstraint({ pattern }, PositionAnchor.BELOW, 0, 8)
            attach(box)
        }

        val row0 = container {
            size = FixedSizeConstraint(354, 14)
            position = AnchorPositionConstraint({ action0 }, PositionAnchor.BELOW, 0, 2)
            attach(box)
        }

        val all = IMessageAction.all()
        val aw = (354 - (all.size - 1) * 4) / all.size
        for ((idx, a) in all.withIndex()) {
            lateinit var outline: OutlineEffect
            var label = TextPrimitive.NONE

            val rect = rectangle {
                size = FixedSizeConstraint(aw, 16)
                position = FixedPositionConstraint(idx * (aw + 4), 0)
                color = Mocha.Surface1.argb

                effect(OutlineEffect {
                    color = Mocha.Overlay0.argb
                }.also { outline = it })

                on<MouseEvent.Press> {
                    cancel()
                    if (button != 0) return@on
                    action = a.id
                    updateActionButtons()
                    updateValueState()
                }

                on<MouseEvent.Move.Enter> {
                    if (action != a.id) color = Mocha.Surface2.argb
                }

                on<MouseEvent.Move.Exit> {
                    if (action != a.id) color = Mocha.Surface1.argb
                }

                attach(row0)
                adopt(text {
                    text = a.name.literal()
                    color = Mocha.Text.argb
                    shadow = false
                    position = CenterPositionConstraint()
                }.also { label = it })
            }

            actions.add(ActionButton(rect, label, outline, a.id))
        }

        `value$label` = text {
            text = "Value".literal()
            color = Mocha.Overlay0.argb
            position = AnchorPositionConstraint({ row0 }, PositionAnchor.BELOW, 0, 8)
            attach(box)
        }

        text {
            text = "Category".literal()
            color = Mocha.Subtext0.argb
            position = AnchorPositionConstraint({ row0 }, PositionAnchor.BELOW, 184, 8)
            attach(box)
        }

        value = textField {
            size = FixedSizeConstraint(170, 16)
            position = AnchorPositionConstraint({ `value$label` }, PositionAnchor.BELOW, 0, 2)
            placeholder = "Action value"
            attach(box)
        }

        `value$disabled` = rectangle {
            size = FixedSizeConstraint(170, 16)
            position = AnchorPositionConstraint({ `value$label` }, PositionAnchor.BELOW, 0, 2)
            color = Mocha.Crust.argb

            effect(OutlineEffect {
                color = Mocha.Surface0.argb
            })

            interact = false
            visible = true
            attach(box)
        }

        `checkbox$category` = multiCheckbox {
            size = FixedSizeConstraint(170, 16)
            position = AnchorPositionConstraint({ `value$label` }, PositionAnchor.BELOW, 184, 2)
            items = listOf("Uncategorized")

            selected {
                if (it == 0) category.isEmpty() else categories.getOrNull(it - 1) == category
            }

            select {
                category = if (it == 0) "" else categories.getOrElse(it - 1) { "" }
                `checkbox$category`.text = category.ifEmpty { "Uncategorized" }
            }

            attach(box)
        }

        `cancel$box` = rectangle {
            size = FixedSizeConstraint(14, 14)
            position = AnchorPositionConstraint({ `value$label` }, PositionAnchor.BELOW, 0, 27)
            color = Mocha.Base.argb

            effect(OutlineEffect {
                color = Mocha.Overlay0.argb
            }.also { `cancel$box$outline` = it })

            on<MouseEvent.Press> {
                cancel()
                if (button != 0) return@on
                cancel = !cancel
                `cancel$box$outline`.color = if (cancel) Mocha.Red.argb else Mocha.Overlay0.argb
                `cancel$inner`.visible = cancel
            }

            attach(box)
            adopt(rectangle {
                size = FixedSizeConstraint(8, 8)
                position = CenterPositionConstraint()
                color = Mocha.Red.argb
                interact = false
                visible = false
            }.also { `cancel$inner` = it })
        }

        text {
            text = "Cancel message".literal()
            color = Mocha.Subtext0.argb
            position = AnchorPositionConstraint({ `cancel$box` }, PositionAnchor.RIGHT, 4, 3)
            attach(box)
        }

        delay = textField {
            size = FixedSizeConstraint(170, 16)
            position = AnchorPositionConstraint({ `value$label` }, PositionAnchor.BELOW, 184, 26)
            placeholder = "Delay (seconds)"

            on<KeyEvent.Type> {
                if (char.code < 32) return@on
                if (char.code == 127) return@on
                if (char == '.') return@on
                if (char.isDigit()) return@on

                cancel()
            }

            attach(box)
        }

        text {
            text = $$"Regex: use $0 for full message, $1, $2... for groups".literal()
            color = Mocha.Overlay0.argb
            position = FixedPositionConstraint(16, 208)
            attach(box)
        }

        val bottom = rectangle {
            size = MixedSizeConstraint(PercentSizeConstraint(100f, 0f), FixedSizeConstraint(0, 1))
            position = FixedPositionConstraint(0, 220)
            color = Mocha.Surface0.argb
            attach(box)
        }

        val cancel0 = rectangle {
            size = FixedSizeConstraint(170, 22)
            position = AnchorPositionConstraint({ bottom }, PositionAnchor.BELOW, 16, 8)
            color = Mocha.Surface1.argb

            effect(OutlineEffect {
                color = Mocha.Red.argb
            })

            on<MouseEvent.Press> {
                if (button == 0) onClose()
                cancel()
            }

            on<MouseEvent.Move.Enter> {
                color = Mocha.Surface2.argb
            }

            on<MouseEvent.Move.Exit> {
                color = Mocha.Surface1.argb
            }

            attach(box)
            adopt(text {
                text = "Cancel".literal()
                color = Mocha.Red.argb
                position = CenterPositionConstraint()
            })
        }

        rectangle {
            size = FixedSizeConstraint(170, 22)
            position = AnchorPositionConstraint({ cancel0 }, PositionAnchor.RIGHT, 8)
            color = Mocha.Surface1.argb

            effect(OutlineEffect {
                color = Mocha.Green.argb
            })

            on<MouseEvent.Press> {
                if (button != 0) return@on cancel()

                val pattern = pattern.value.trim()
                if (pattern.isEmpty()) return@on cancel()

                val delay = delay.value.toDoubleOrNull() ?: 0.0
                val new = ActionEntry(pattern, match, action, value.value, entry?.entry?.enabled ?: true, category, cancel, delay)

                if (entry == null) MessageActions.add(new.pattern, new.match, new.id, new.value, new.category, new.cancel, new.delay)
                else MessageActions.update(entry!!.index, new)

                onClose()
                cancel()
            }

            on<MouseEvent.Move.Enter> {
                color = Mocha.Surface2.argb
            }

            on<MouseEvent.Move.Exit> {
                color = Mocha.Surface1.argb
            }

            attach(box)
            adopt(text {
                text = "Save".literal()
                color = Mocha.Green.argb
                position = CenterPositionConstraint()
            })
        }
    }

    fun open(entry: ActionEntryData?, selectedCategory: String?) {
        this.entry = entry

        if (entry != null) {
            pattern.value = entry.entry.pattern
            pattern.cursor = pattern.value.length
            match = entry.entry.match
            action = entry.entry.id
            value.value = entry.entry.value
            value.cursor = value.value.length
            cancel = entry.entry.cancel
            category = entry.entry.category
            delay.value = if (entry.entry.delay > 0.0) entry.entry.delay.toString() else ""
            delay.cursor = delay.value.length
        } else {
            pattern.reset(true)
            match = MatchType.CONTAINS
            action = 0
            value.reset(true)
            cancel = false
            category = selectedCategory ?: ""
            delay.reset(true)
        }

        title.text = (if (entry == null) "Create Action" else "Edit Action").literal()

        categories = MessageActions.categories.map { it.name }
        `checkbox$category`.items = listOf("Uncategorized") + categories
        `checkbox$category`.text = category.ifEmpty { "Uncategorized" }
        `checkbox$match`.text = match.displayName

        updateActionButtons()
        updateValueState()

        `cancel$box$outline`.color = if (cancel) Mocha.Red.argb else Mocha.Overlay0.argb
        `cancel$inner`.visible = cancel

        gui.scene.focused = this
    }

    private fun updateActionButtons() {
        for (btn in actions) {
            val selected = btn.id == action
            btn.rect.color = if (selected) Mocha.Lavender.argb else Mocha.Surface1.argb
            btn.outline.color = if (selected) Mocha.Lavender.argb else Mocha.Overlay0.argb
            btn.text.color = if (selected) Mocha.Base.argb else Mocha.Text.argb
        }
    }

    private fun updateValueState() {
        val disabled = action == 0
        value.visible = !disabled
        `value$disabled`.visible = disabled
        `value$label`.color = if (disabled) Mocha.Overlay0.argb else Mocha.Subtext0.argb
        `value$label`.text = (if (disabled) "Value" else IMessageAction.all().firstOrNull { it.id == action }?.name ?: "Value").literal()
    }
}
