package ai.sterling.kchat.android.databinding.adapters.typecomponents

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.soywiz.klock.DateTime

interface DateTimeComponent {

    @BindingAdapter("android:text")
    fun bindDate(view: TextView, date: DateTime?)

    @BindingAdapter("android:text")
    fun bindTime(view: TextView, time: DateTime?)
}
