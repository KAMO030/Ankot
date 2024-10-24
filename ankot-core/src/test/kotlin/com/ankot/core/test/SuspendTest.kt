package com.ankot.core.test

import com.ankot.core.application.AnkotApp
import com.ankot.core.component.Kind
import com.ankot.core.component.inject
import com.ankot.core.dsl.componentOf
import com.ankot.core.dsl.module
import com.ankot.core.dsl.runAnkotApp

suspend fun main(args: Array<String>) {
    val app: AnkotApp by inject()

    val module = module {
        // 有两个String 一个名字默认为s1，另一个为s2
        component("s1", kind = Kind.Prototype) {
            println(11)
            val r = "123"
            println(11)
            r
        }
        component("s2", isLateInit = true ) {
            println(12)
            val r = "456"
            println(12)
            r
        }
        // 拿名字为s1的String类型的实例注入，先by类型再by名字
        component("s3", isLateInit = true) {
            println(21)
            val r = BigString2(get("s1"))
            println(21)
            r
        }
        // 拿名字为s2的String类型的实例注入，先by类型再by名字
        component("s4", isLateInit = true) {
            println(22)
            val r = BigString2(get("s2"))
            println(22)
            r
        }
        // 拿名字为s3的BigString2类型的实例注入，先by类型再by名字
        component("s5", isLateInit = true) {
            println(31)
            val r = BigString3(get("s3"))
            println(r.s.s)
            r
        }
        // 拿名字为s4的BigString2类型的实例注入，先by类型再by名字
        component("s6", isLateInit = true) {
            println(32)
            val r = BigString3(get("s4"))
            println(r.s.s)
            r
        }
        componentOf(::BigString4)
    }

    runAnkotApp(args) {
        modules(module)
    }
    val bigString4: BigString4 = app.get()
    assert(bigString4.bigString3.s.s == "456")
    println(app)
    // Prototype test
    val s1 :String= app.get("s1")
    val s11 :String= app.get("s1")

}

open class BigString1(val s: String)

class BigString2(s: String) : BigString1(s)

class BigString3(val s: BigString1)

class BigString4 {
    val bigString3: BigString3 by inject("s6")
}



