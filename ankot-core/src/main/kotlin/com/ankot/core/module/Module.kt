package com.ankot.core.module

import com.ankot.core.component.ComponentProvider
import com.ankot.core.component.Kind
import com.ankot.core.component.Kind.Single
import com.ankot.core.component.createProvider
import com.ankot.core.const.ComponentConstructor
import com.ankot.core.ext.geSimpleName
import kotlin.reflect.KClass

class Module(
    @PublishedApi
    internal val isLateInit: Boolean = false,
) {
    internal var preInitComponents = hashSetOf<ComponentProvider<Any>>()

    val componentMappings = hashMapOf<String, ComponentProvider<*>>()
    internal val importedModules: MutableList<Module> = mutableListOf()

    @PublishedApi
    internal inline fun <reified T : Any> component(
        name: String = _tn<T>(),
        kind: Kind = Single,
        isLateInit: Boolean = this.isLateInit,
        noinline constructor: ComponentConstructor<T>
    ) {
        val componentProvider = createProvider(T::class, name, kind, isLateInit, constructor)
        componentMappings.merge(name, componentProvider) { _, _ -> error("name : $name component already exists") }
        addPreInitComponent(componentProvider as ComponentProvider<Any>, isLateInit)
    }

    @PublishedApi
    internal fun addPreInitComponent(componentProvider: ComponentProvider<Any>, isLateInit: Boolean) {
        if ((!isLateInit) && componentProvider.componentDefinition.kind == Single) {
            preInitComponents += componentProvider
        }
    }


    fun import(vararg modules: Module) = importedModules.addAll(modules)


    operator fun plus(module: Module) = listOf(this, module)

    operator fun plus(modules: List<Module>) = listOf(this) + modules

    operator fun Module.unaryPlus(): Module {
        this@Module.importedModules.add(this)
        return this
    }

}


fun flatModules(modules: List<Module>, newModules: MutableSet<Module> = mutableSetOf()): Set<Module> {
    return newModules.apply {
        modules.forEach {
            this += it
            flatModules(it.importedModules, newModules)
        }
    }
}

inline fun <reified T> _tn(): String = T::class.geSimpleName()

fun _tn(clazz: KClass<*>): String = clazz.geSimpleName()



