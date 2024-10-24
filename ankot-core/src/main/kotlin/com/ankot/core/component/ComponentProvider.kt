package com.ankot.core.component

import com.ankot.core.const.ComponentConstructor
import com.ankot.core.module._tn
import kotlin.reflect.KClass

class ComponentProvider<T : Any>(val componentDefinition: ComponentDef<T>, val constructor: ComponentConstructor<T>) {


    var value: T? = null


    private suspend fun create(collector: ComponentCollector): T {
        val component = constructor(collector)
        value = component
        return component
    }

    fun isCreated(): Boolean = when (componentDefinition.kind) {
        Kind.Single -> value != null
        Kind.Prototype -> false
    }

    suspend fun get(collector: ComponentCollector): T =
        if (isCreated()) {
            value!!
        } else {
            create(collector)
        }


    fun destroy(): T {
        TODO()
    }


}

fun <T : Any> createProvider(
    type: KClass<T>,
    name: String = _tn(type),
    kind: Kind = Kind.Single,
    isLateInit:Boolean = false,
    constructor: ComponentConstructor<T>
): ComponentProvider<T> {
    val componentDef = ComponentDef(name, kind, type, isLateInit)
    return ComponentProvider(componentDef, constructor)
}

inline fun <reified T : Any> createProvider(
    value: T ,
    name: String = _tn<T>(),
    kind: Kind = Kind.Single,
    isLateInit:Boolean = false
): ComponentProvider<T> {
    return createProvider(T::class, name, kind, isLateInit) { value }
}
