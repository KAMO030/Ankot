package com.ankot.core.const

import com.ankot.core.application.AnkotApp
import com.ankot.core.component.ComponentCollector
import com.ankot.core.module.Module

typealias ModuleDeclaration = Module.()->Unit

typealias AnkotAppDeclaration = AnkotApp.() -> Unit

typealias ComponentConstructor<T> = suspend ComponentCollector.() -> T

