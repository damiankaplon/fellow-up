package io.fellowup.infrastructure.db.reflection

fun setPrivateProperty(instance: Any, propertyName: String, value: Any?) {
    val property = instance::class.java.getDeclaredField(propertyName)
    val isAccessible = property.canAccess(instance)
    property.isAccessible = true
    property.set(instance, value)
    property.isAccessible = isAccessible
}

inline fun <reified T, reified R> T.getPrivateProperty(propertyName: String): R {
    return T::class.java.getDeclaredField(propertyName).let { field ->
        val isAccessible = field.canAccess(this)
        field.isAccessible = true
        val result = field.get(this) as? R
            ?: error("Property ${T::class.simpleName}::$propertyName is not of type ${R::class.simpleName}")
        field.isAccessible = isAccessible
        result
    }
}
