package ai.sterling.kchat.android.platform.internal

import android.content.SharedPreferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

private inline fun <T> SharedPreferences.delegate(
    key: String,
    defaultValue: T?,
    crossinline getter: SharedPreferences.(String, T?) -> T?,
    crossinline setter: SharedPreferences.Editor.(String, T) -> SharedPreferences.Editor
): ReadWriteProperty<Any, T?> {
    return object : ReadWriteProperty<Any, T?> {
        override fun getValue(thisRef: Any, property: KProperty<*>) =
            getter(key, defaultValue)

        override fun setValue(thisRef: Any, property: KProperty<*>, value: T?) =
            edit().apply {
                if (value == null) {
                    remove(key)
                } else {
                    setter(key, value)
                }
            }.apply()
    }
}

fun SharedPreferences.bool(key: String) =
    delegate(key, false, { s: String, b: Boolean? -> getBoolean(key, false) }, SharedPreferences.Editor::putBoolean)

fun SharedPreferences.int(key: String) =
    delegate(key, Int.MIN_VALUE, { s: String, i: Int? -> getInt(key, Int.MIN_VALUE) }, SharedPreferences.Editor::putInt)

fun SharedPreferences.int(key: String, defaultValue: Int) =
    delegate(key, defaultValue, { s: String, i: Int? -> getInt(key, defaultValue) }, SharedPreferences.Editor::putInt)

fun SharedPreferences.long(key: String) =
    delegate(key, Long.MIN_VALUE, { s: String, l: Long? -> getLong(key, Long.MIN_VALUE) }, SharedPreferences.Editor::putLong)

fun SharedPreferences.string(key: String) =
    delegate(key, null, { s: String, st: String? -> getString(key, null) }, SharedPreferences.Editor::putString)

fun SharedPreferences.string(key: String, defaultValue: String) =
    delegate(key, defaultValue, { s: String, st: String? -> getString(key, defaultValue) }, SharedPreferences.Editor::putString)
