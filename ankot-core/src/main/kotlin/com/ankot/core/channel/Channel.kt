package com.ankot.core.channel

import java.util.UUID
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit


fun main(args: Array<String>) {
    val syncChannel1 = AsyncChannel()
    val syncChannel2 = DelayChannel(3, TimeUnit.SECONDS)

    val publisher = Publisher()
    publisher register syncChannel1
    publisher register syncChannel1

    Subscriber("test", "t 2", "t 1") { it: Int ->
        println(it)
    } subscribed syncChannel1

//    Subscriber("test", "t 2", "t1") { it:Int->
//        println(it.toString()+"12331")
//    } subscribed syncChannel2

//    publisher publish Message("test", 123, "  t1 | t 2 ")
    syncChannel2.receive("test") { it: Int ->
        println(it.toString() + "6546")
    }

    Message("test", 456,"21") send syncChannel2
    Message("test", 123) send syncChannel1
}

class Publisher {

    private val channels: MutableSet<AbsChannel> by lazy(::mutableSetOf)

    infix fun register(channel: AbsChannel) = channels.add(channel)

    infix fun publish(msg: Triple<String, Nothing, String>) = publish(msg.first, msg.second, msg.third)

    fun publish(topic: String, data: Nothing, tags: String = "") = publish(Message(topic, data, tags))

    infix fun <T : Any> publish(msg: Message<T>) = channels.forEach { it.notify(msg) }

}

class Message<T : Any>(
    val topic: String,
    val data: T,
    val tags: String = ""
)

class Subscriber<T>(
    private val topic: String,
    vararg val tags: String = emptyArray(),
    val rec: (T) -> Unit
) {
    infix fun subscribed(channel: AbsChannel) = channel.subscribers.getOrPut(topic, ::mutableListOf).add(this)
    fun receive(data: Any) = rec(data as T)

}

abstract class AbsChannel {

    val id: String by lazy(UUID::randomUUID::toString)

    val subscribers: MutableMap<String, MutableList<Subscriber<*>>> by lazy(::linkedMapOf)

    protected fun selectSub(
        topic: String,
        tags: String = ""
    ): List<Subscriber<*>> =
        when {
            tags.contains('|') -> tags.split('|')
            tags.contains('&') -> tags.split('&')
            tags.isNotBlank() -> listOf(tags)
            else -> emptyList()
        }.map(String::trim).let { splitTag ->
            when {
                tags.contains('|') -> { it: Collection<String> ->
                    it.any { tag -> splitTag.contains(tag) }
                }

                tags.contains('&') -> { it: Collection<String> ->
                    it.containsAll(splitTag)
                }

                tags.isNotBlank() -> { it: Collection<String> ->
                    it.contains(splitTag[0])
                }

                else -> { _ -> true }
            }
        }.let { match ->
            subscribers.getOrElse(topic, ::emptyList)
                .filter { it.tags.isEmpty() || match(it.tags.map(String::trim).toSet()) }.toList()
        }


    abstract fun <T : Any> notify(msg: Message<T>)

    fun <T> receive(topic: String, vararg tags: String = emptyArray(), rec: (T) -> Unit) {
        Subscriber(topic, *tags, rec = rec).subscribed(this)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as AbsChannel
        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

class SyncChannel : AbsChannel() {

    override fun <T : Any> notify(msg: Message<T>) =
        selectSub(msg.topic, msg.tags).forEach { it.receive(msg.data) }
}

class AsyncChannel : AbsChannel() {
    private val pool: ExecutorService by lazy(Executors::newSingleThreadExecutor)
    override fun <T : Any> notify(msg: Message<T>) {
        pool.execute { selectSub(msg.topic, msg.tags).forEach { it.receive(msg.data) } }
    }


}

class DelayChannel(private val delay: Long, private val unit: TimeUnit) : AbsChannel() {
    private val pool: ScheduledExecutorService by lazy { ScheduledThreadPoolExecutor(1) }
    override fun <T : Any> notify(msg: Message<T>) {
        pool.schedule({ selectSub(msg.topic, msg.tags).forEach { it.receive(msg.data) } }, delay, unit)
    }
}

infix fun Message<*>.send(channel: AbsChannel) {
    channel.notify(this)
}
