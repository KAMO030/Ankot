package com.ankot.core.component

import com.ankot.core.module._tn
import kotlin.coroutines.*
import kotlin.reflect.KClass

class ComponentCollector(private val context: CoroutineContext) {

    private val providers: MutableMap<String, ComponentProvider<Any>> = HashMap()

    private val componentCoroutine: MutableMap<String, MutableList<Continuation<Any>>> = mutableMapOf()

    suspend inline fun <reified T : Any> get(
        name: String = _tn<T>()
    ): T = get(T::class, name)

    suspend fun <T : Any> get(
        type: KClass<T>,
        name: String = _tn(type)
    ): T {
        val componentName = findComponentName(type, name)
        val provider = providers[componentName]

        return getComponent(provider, componentName)
    }

    private suspend fun <T : Any> getComponent(
        provider: ComponentProvider<*>?,
        componentName: String
    ): T {
        val definition = provider?.componentDefinition
        return if (definition != null && (provider.isCreated() || definition.isLateInit || definition.kind == Kind.Prototype)
        ) {
            provider.get(this) as T
        } else {
            suspendCoroutine {
                componentCoroutine.computeIfAbsent(componentName) { arrayListOf() }.add(it as Continuation<Any>)
            }
        }
    }

    fun <T : Any> use(type: KClass<T>, name: String, block: ((T) -> Unit)? = null) {
        suspend {
            get(type, name)
        }.startCoroutine(Continuation(context) { result ->
            val value = result.getOrThrow()
            block?.invoke(value)
        })
    }


    fun <T : Any> initComponent(provider: ComponentProvider<T>) {
        suspend {
            provider.get(this)
        }.startCoroutine(Continuation(context) { result ->
            val value = result.getOrThrow()
            componentCoroutine.remove(provider.componentDefinition.name)?.forEach { it.resume(value) }
        })
    }

    fun isDone() = componentCoroutine.isEmpty()

    @PublishedApi
    internal fun findComponentName(type: KClass<*> = Any::class, name: String): String =
        if (type == Any::class) {
            if (providers.contains(name)) name else null
        } else {
            providers.filter { type.java.isAssignableFrom(it.value.componentDefinition.primaryType.java) }.run {
                this.keys.find { size == 1 || it == name }
            }
        } ?: name

    fun mergeProvider(
        provider: ComponentProvider<*>,
        name: String = provider.componentDefinition.name
    ): ComponentProvider<Any> =
        providers.merge(name, provider as ComponentProvider<Any>)
        { _, _ -> error("name : $name component already exists") }!!


}