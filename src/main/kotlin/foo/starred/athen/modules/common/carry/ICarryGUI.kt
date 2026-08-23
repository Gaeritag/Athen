package foo.starred.athen.modules.common.carry

import foo.starred.athen.ui.themes.Catppuccin.Mocha
import foo.starred.cascade.constraints.impl.data.PositionAlignment
import foo.starred.cascade.constraints.impl.position.AlignPositionConstraint
import foo.starred.cascade.constraints.impl.position.CenterPositionConstraint
import foo.starred.cascade.constraints.impl.position.FixedPositionConstraint
import foo.starred.cascade.constraints.impl.size.FillSizeConstraint
import foo.starred.cascade.constraints.impl.size.FixedSizeConstraint
import foo.starred.cascade.constraints.impl.size.MixedSizeConstraint
import foo.starred.cascade.constraints.impl.size.PercentSizeConstraint
import foo.starred.cascade.effects.impl.OutlineEffect
import foo.starred.cascade.events.impl.MouseEvent
import foo.starred.cascade.graphics.font.CascadeFonts
import foo.starred.cascade.primitives.impl.ContainerPrimitive.Companion.container
import foo.starred.cascade.primitives.impl.RectanglePrimitive.Companion.rectangle
import foo.starred.cascade.primitives.impl.ScrollablePrimitive
import foo.starred.cascade.primitives.impl.ScrollablePrimitive.Companion.scrollable
import foo.starred.cascade.primitives.impl.TextPrimitive
import foo.starred.cascade.primitives.impl.TextPrimitive.Companion.text
import foo.starred.cascade.screen.CascadeScreen
import foo.starred.cascade.wrappers.text.impl.CascadeTextWrapper
import foo.starred.snowbird.utils.brighten
import foo.starred.snowbird.utils.literal
import foo.starred.snowbird.utils.plural
import foo.starred.snowbird.utils.toDuration

abstract class ICarryGUI<T : ITrackedCarry>(val screenName: String) : CascadeScreen(screenName) {
    private val tooltips = mutableListOf<TooltipEntry>()
    private var list: ScrollablePrimitive
    private lateinit var badge: TextPrimitive
    private lateinit var tip: TextPrimitive

    protected abstract fun carries(): Map<String, T>
    protected abstract fun persist()
    protected abstract fun remove(player: String)

    init {
        container {
            size = FillSizeConstraint()
            position = FixedPositionConstraint(0, 0)
            interact = false
            attach(scene)
        }

        val main = container {
            size = FixedSizeConstraint(520, 330)
            position = CenterPositionConstraint()
            attach(scene)
        }

        rectangle {
            size = FixedSizeConstraint(520, 36)
            position = FixedPositionConstraint(0, 0)
            color = Mocha.Base.argb

            effect(OutlineEffect {
                color = Mocha.Surface0.argb
            })

            attach(main)

            adopt(text {
                wrapper = CascadeTextWrapper
                text = screenName.literal()
                textSize = 10f
                color = Mocha.Text.argb
                position = AlignPositionConstraint(PositionAlignment.START, PositionAlignment.CENTER, 14, 0)
            })

            adopt(text {
                wrapper = CascadeTextWrapper
                text = "".literal()
                textSize = 8f
                color = Mocha.Subtext0.argb
                position = AlignPositionConstraint(PositionAlignment.END, PositionAlignment.CENTER, -14, 0)
            }.also { badge = it })
        }

        val body = rectangle {
            size = FixedSizeConstraint(520, 250)
            position = FixedPositionConstraint(0, 42)
            color = Mocha.Base.argb

            effect(OutlineEffect {
                color = Mocha.Surface0.argb
            })

            attach(main)
        }

        list = scrollable {
            size = FillSizeConstraint(6)
            position = CenterPositionConstraint()
            attach(body)
        }

        rectangle {
            size = FixedSizeConstraint(520, 30)
            position = FixedPositionConstraint(0, 298)
            color = Mocha.Base.argb

            effect(OutlineEffect {
                color = Mocha.Surface0.argb
            })

            attach(main)

            adopt(text {
                wrapper = CascadeTextWrapper
                text = "Left click +1/-1 for completed  •  Right click for total".literal()
                textSize = 8f
                color = Mocha.Subtext0.argb
                position = AlignPositionConstraint(PositionAlignment.START, PositionAlignment.CENTER, 14, 0)
            }.also { tip = it })
        }

        fn()
    }

    fun fn() {
        list.children.clear()
        val entries = carries().values.toList()

        badge.text = "${entries.size} Active".literal()
        footer()

        if (entries.isEmpty()) {
            text {
                wrapper = CascadeTextWrapper
                text = "No carries being tracked".literal()
                textSize = 9f
                color = Mocha.Subtext0.argb
                position = CenterPositionConstraint()
                attach(list)
            }

            return
        }

        var cy = 2
        val now = System.currentTimeMillis()

        for (carry in entries) {
            val row = rectangle {
                size = MixedSizeConstraint(PercentSizeConstraint(100f, 0f), FixedSizeConstraint(0, 44))
                position = FixedPositionConstraint(0, cy)
                color = Mocha.Surface0.withAlpha(0.35f)

                effect(OutlineEffect {
                    color = Mocha.Surface0.argb
                })

                on<MouseEvent.Move.Enter> {
                    color = Mocha.Surface0.withAlpha(0.6f)
                }

                on<MouseEvent.Move.Exit> {
                    color = Mocha.Surface0.withAlpha(0.35f)
                }

                attach(list)
            }

            text {
                wrapper = CascadeTextWrapper
                text = carry.player.literal()
                textSize = 9f
                color = Mocha.Text.argb
                position = AlignPositionConstraint(PositionAlignment.START, PositionAlignment.CENTER, 12, -7)
                attach(row)
            }

            rectangle {
                val width = CascadeFonts.arial.width(carry.short, 7.5f) + 10f
                size = FixedSizeConstraint(width, 14f)
                position = AlignPositionConstraint(PositionAlignment.START, PositionAlignment.CENTER, (CascadeFonts.arial.width(carry.player, 9f) + 18f).toInt(), -7)
                color = Mocha.Surface1.argb

                effect(OutlineEffect {
                    color = Mocha.Surface2.argb
                })

                attach(row)

                adopt(text {
                    wrapper = CascadeTextWrapper
                    text = carry.short.literal()
                    textSize = 7.5f
                    color = Mocha.Lavender.argb
                    position = CenterPositionConstraint()
                })
            }

            text {
                val time = carry.lastCompletionTime.takeIf { it != 0L }?.let { ((now - it) / 1000.0).toDuration() } ?: "N/A"
                val rate = ((now - carry.firstCompletionTime) / 1000).takeIf { carry.completed > 2 && carry.firstCompletionTime != 0L && it > 0 }?.let { "${carry.completed * 3600 / it}/hr" } ?: "N/A"

                wrapper = CascadeTextWrapper
                text = "${carry.completed}/${carry.total} completed  •  $time  •  $rate".literal()
                textSize = 7.5f
                color = Mocha.Subtext0.argb
                position = AlignPositionConstraint(PositionAlignment.START, PositionAlignment.CENTER, 12, 9)
                attach(row)
            }

            var offset = -12f
            rectangle {
                size = FixedSizeConstraint(22f, 22f)
                position = AlignPositionConstraint(PositionAlignment.END, PositionAlignment.CENTER, offset.toInt(), 0)
                color = Mocha.Surface1.argb

                effect(OutlineEffect {
                    color = Mocha.Surface2.argb
                })

                on<MouseEvent.Press> {
                    cancel()
                    if (button != 0) return@on
                    remove(carry.player)
                    persist()
                    add(carry.player, 0, TooltipEntry.ActionType.CARRY_REMOVED)
                    fn()
                }

                on<MouseEvent.Move.Enter> {
                    color = Mocha.Red.argb.brighten(0.8f)
                }

                on<MouseEvent.Move.Exit> {
                    color = Mocha.Surface1.argb
                }

                attach(row)
                adopt(text {
                    wrapper = CascadeTextWrapper
                    text = "×".literal()
                    textSize = 9f
                    color = Mocha.Text.argb
                    position = CenterPositionConstraint()
                })
            }

            offset -= 28f

            rectangle {
                size = FixedSizeConstraint(22f, 22f)
                position = AlignPositionConstraint(PositionAlignment.END, PositionAlignment.CENTER, offset.toInt(), 0)
                color = Mocha.Surface1.argb

                effect(OutlineEffect {
                    color = Mocha.Surface2.argb
                })

                on<MouseEvent.Press> {
                    cancel()
                    when (button) {
                        0 -> if (carry.completed > 0) {
                            carry.completed--
                            persist()
                            add(carry.player, 1, TooltipEntry.ActionType.COUNT_DECREASE)
                            fn()
                        }
                        1 -> if (carry.total > 1) {
                            carry.total--
                            persist()
                            add(carry.player, 1, TooltipEntry.ActionType.TOTAL_DECREASE)
                            fn()
                        }
                    }
                }

                on<MouseEvent.Move.Enter> {
                    color = Mocha.Peach.argb.brighten(0.8f)
                }

                on<MouseEvent.Move.Exit> {
                    color = Mocha.Surface1.argb
                }

                attach(row)
                adopt(text {
                    wrapper = CascadeTextWrapper
                    text = "-1".literal()
                    textSize = 7.5f
                    color = Mocha.Text.argb
                    position = CenterPositionConstraint()
                })
            }

            offset -= 28f

            rectangle {
                size = FixedSizeConstraint(22f, 22f)
                position = AlignPositionConstraint(PositionAlignment.END, PositionAlignment.CENTER, offset.toInt(), 0)
                color = Mocha.Surface1.argb

                effect(OutlineEffect {
                    color = Mocha.Surface2.argb
                })

                on<MouseEvent.Press> {
                    cancel()
                    when (button) {
                        0 -> {
                            if (carry.completed < carry.total) {
                                carry.completed++
                                persist()
                                add(carry.player, 1, TooltipEntry.ActionType.COUNT_INCREASE)
                                fn()
                            }
                        }
                        1 -> {
                            carry.total++
                            persist()
                            add(carry.player, 1, TooltipEntry.ActionType.TOTAL_INCREASE)
                            fn()
                        }
                    }
                }

                on<MouseEvent.Move.Enter> {
                    color = Mocha.Green.argb.brighten(0.8f)
                }

                on<MouseEvent.Move.Exit> {
                    color = Mocha.Surface1.argb
                }

                attach(row)
                adopt(text {
                    wrapper = CascadeTextWrapper
                    text = "+1".literal()
                    textSize = 7.5f
                    color = Mocha.Text.argb
                    position = CenterPositionConstraint()
                })
            }

            cy += 48
        }
    }

    override fun init() {
        super.init()
        fn()
    }

    override fun onClose() {
        persist()
        super.onClose()
    }

    protected fun add(player: String, amount: Int, action: TooltipEntry.ActionType) {
        val now = System.currentTimeMillis()
        val active = tooltips.lastOrNull { it.player == player && it.action == action && action.group != -1 && now - it.timestamp < 2000 }

        if (active != null) {
            active.amount += amount
            active.timestamp = now
            footer()
            return
        }

        if (tooltips.size >= 5) tooltips.removeAt(0)
        tooltips.add(TooltipEntry(player, amount, action))
        footer()
    }

    private fun footer() {
        val last = tooltips.lastOrNull()
        val bool = last != null && System.currentTimeMillis() - last.timestamp < 4000

        tip.text = if (bool) last.display().literal() else "Left click +1/-1 for completed  •  Right click for total".literal()
        tip.color = if (bool) Mocha.Lavender.argb else Mocha.Subtext0.argb
    }

    data class TooltipEntry(val player: String, var amount: Int, val action: ActionType, var timestamp: Long = System.currentTimeMillis()) {
        enum class ActionType(val group: Int) {
            COUNT_INCREASE(0),
            COUNT_DECREASE(0),
            TOTAL_INCREASE(1),
            TOTAL_DECREASE(1),
            CARRY_REMOVED(-1);
        }

        fun display(): String {
            val carry = amount.plural("carry", "carries")
            return when (action) {
                ActionType.COUNT_INCREASE -> "Added $amount completed $carry for $player"
                ActionType.COUNT_DECREASE -> "Decreased $amount completed $carry for $player"
                ActionType.TOTAL_INCREASE -> "Added $amount $carry for $player"
                ActionType.TOTAL_DECREASE -> "Decreased $amount $carry for $player"
                ActionType.CARRY_REMOVED -> "Removed $player from tracking"
            }
        }
    }
}