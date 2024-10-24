package com.ankot.core.component


import kotlin.reflect.KClass

class ComponentDef<T : Any>(
    val name: String,
    val kind: Kind,
    val primaryType: KClass<T>,
    val isLateInit: Boolean
) {


}




