package com.ankot.core.dsl

import com.ankot.core.annotation.AnkotDslMarker
import com.ankot.core.application.AnkotApp
import com.ankot.core.const.AnkotAppDeclaration
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@AnkotDslMarker
fun runAnkotApp(args:Array<String> = arrayOf(), context: CoroutineContext = EmptyCoroutineContext, appDeclaration: AnkotAppDeclaration): AnkotApp =
     AnkotApp(context).apply(appDeclaration).apply(AnkotApp::run)

@AnkotDslMarker
fun applyAnkotApp(block: AnkotAppDeclaration) {
     if (AnkotApp.isReady()) {
          AnkotApp.instance!!.block()
     } else {
          AnkotApp.callbacks += block
     }
}