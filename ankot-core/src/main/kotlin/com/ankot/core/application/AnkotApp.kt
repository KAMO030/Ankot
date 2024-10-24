package com.ankot.core.application

import com.ankot.core.component.ComponentCollector
import com.ankot.core.component.ComponentProvider
import com.ankot.core.component.createProvider
import com.ankot.core.const.AnkotAppDeclaration
import com.ankot.core.module.Module
import com.ankot.core.module._tn
import com.ankot.core.module.flatModules
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KClass

class AnkotApp(context: CoroutineContext) {

    private var preInitComponents = hashSetOf<ComponentProvider<Any>>()

    @PublishedApi
    internal val collector = ComponentCollector(context)

    fun modules(vararg modules: Module) =
        flatModules(modules.toList()).forEach {
            it.componentMappings.forEach { (name, cp) ->
                collector.mergeProvider(cp, name)
            }
            preInitComponents += it.preInitComponents
        }

    fun run() {



        val ankotAppProvider = createProvider<AnkotApp>(this)

        collector.mergeProvider(ankotAppProvider)

        collector.initComponent(ankotAppProvider)

        instance = this

        preInitComponents.forEach(collector::initComponent)

        if (!collector.isDone()) error("容器未初始化完成")

        instance = null
    }

    fun <T : Any> use(type: KClass<T>, name: String = _tn(type), block: ((T) -> Unit)? = null) =
        collector.use(type, name, block)

    suspend inline fun <reified T : Any> get(
        name: String = _tn<T>()
    ): T = collector.get(T::class, name)

    companion object {

        internal var instance: AnkotApp? = null
            set(value) {
                field = value
                if (value == null) return
                callbacks.forEach { it.invoke(value) }
                callbacks.clear()
            }

        internal val callbacks: MutableList<AnkotAppDeclaration> by lazy(::arrayListOf)

        fun isReady() = instance != null
    }

}



