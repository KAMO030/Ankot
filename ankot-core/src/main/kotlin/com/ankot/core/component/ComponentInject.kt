package com.ankot.core.component

import com.ankot.core.dsl.applyAnkotApp
import com.ankot.core.module._tn
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty


class ComponentInject<V : Any>(type: KClass<V>, name: String):ReadOnlyProperty<Any?,V> {
    private lateinit var _value: V

    init {
        applyAnkotApp {
            use(type, name) {
                _value = it
            }
        }
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): V = _value

}

inline fun <reified T : Any> inject(name: String = _tn<T>()): ComponentInject<T> =
    ComponentInject(T::class, name)

