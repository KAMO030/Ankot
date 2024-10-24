package com.ankot.core.dsl

import com.ankot.core.annotation.AnkotDslMarker
import com.ankot.core.const.ModuleDeclaration
import com.ankot.core.module.Module


@AnkotDslMarker
fun module(isNotLazy: Boolean = false, moduleDeclaration: ModuleDeclaration): Module =
    Module(isNotLazy).apply(moduleDeclaration)

inline fun <reified E : Any> Module.componentOf(crossinline constructor: () -> E) {
    this.component {
        constructor()
    }
}

inline fun <reified T : Any, reified E : Any> Module.componentOf(crossinline constructor: (T) -> E) {
    this.component {
        constructor(get())
    }
}

inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified E : Any> Module.componentOf(
    crossinline constructor: (T1, T2, T3) -> E
) {
    this.component {
        constructor(get(), get(), get())
    }
}

inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified E : Any> Module.componentOf(
    crossinline constructor: (T1, T2, T3, T4) -> E
) {
    this.component {
        constructor(get(), get(), get(), get())
    }
}

inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified E : Any> Module.componentOf(
    crossinline constructor: (T1, T2, T3, T4, T5) -> E
) {
    this.component {
        constructor(get(), get(), get(), get(), get())
    }
}

inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified E : Any> Module.componentOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6) -> E
) {
    this.component {
        constructor(get(), get(), get(), get(), get(), get())
    }
}

inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified E : Any> Module.componentOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7) -> E
) {
    this.component {
        constructor(get(), get(), get(), get(), get(), get(), get())
    }
}

inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified T8 : Any, reified E : Any> Module.componentOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8) -> E
) {
    this.component {
        constructor(get(), get(), get(), get(), get(), get(), get(), get())
    }
}
