package com.ankot.core.ext

import kotlin.reflect.KClass

fun KClass<*>.getFullName(): String {
    return (classNames[this] ?: saveCache()).first
}

fun KClass<*>.geSimpleName(): String {
    return (classNames[this] ?: saveCache()).second
}


fun KClass<*>.saveCache(): Pair<String,String> {
    val fullName = this.qualifiedName ?: "KClass@${this.hashCode()}"
    val simpleName = this.simpleName ?: "KClass@${this.hashCode()}"

    val namePair = fullName to simpleName
    classNames[this] = namePair
    return namePair
}



private val classNames: MutableMap<KClass<*>, Pair<String,String>> = hashMapOf()

