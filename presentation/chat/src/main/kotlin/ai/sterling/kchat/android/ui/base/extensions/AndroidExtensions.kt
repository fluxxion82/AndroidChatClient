package ai.sterling.kchat.android.ui.base.extensions

import androidx.fragment.app.Fragment

inline fun <reified T> Fragment.findCallback(): T? {
    var parent = parentFragment
    while (parent != null) {
        if (parent is T) {
            return parent
        }
        parent = parent.parentFragment
    }

    return activity as? T ?: activity?.application as? T
}

val Any.TAG: String
    get() = this::class.java.name
