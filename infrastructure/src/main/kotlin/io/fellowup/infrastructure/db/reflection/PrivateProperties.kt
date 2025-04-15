package io.fellowup.infrastructure.db.reflection

fun setPrivateProperty(instance: Any, propertyName: String, value: Any?) {
    val property = instance::class.java.getDeclaredField(propertyName)
    property.isAccessible = true
    property.set(instance, value)
    property.isAccessible = false
}

inline fun <reified T, reified R> T.getPrivateProperty(propertyName: String): R {
    return T::class.java.getDeclaredField(propertyName).let { field ->
        field.isAccessible = true
        field.get(this) as? R
            ?: error("Property ${T::class.simpleName}::$propertyName is not of type ${R::class.simpleName}")
    }
}

