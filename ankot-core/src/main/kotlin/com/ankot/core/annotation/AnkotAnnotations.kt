package com.ankot.core.annotation

@DslMarker
annotation class AnkotDslMarker


@RequiresOptIn(message = "Used to extend current API with Ankot API. Shouldn't be used outside of Ankot API", level = RequiresOptIn.Level.ERROR)
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
annotation class AnkotInternalApi