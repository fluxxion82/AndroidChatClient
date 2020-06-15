package ai.sterling.kchat.android.databinding.adapters.typecomponents

import android.widget.TextView
import com.soywiz.klock.DateFormat
import com.soywiz.klock.DateTime
import javax.inject.Inject

internal class JavaDateFormatter @Inject constructor() : DateTimeComponent {

    private val formatter by lazy {
        DateFormat("dd MMMM yyyy")
    }

    private val timeFormatter by lazy {
        DateFormat("hh:mma")
    }

    override fun bindDate(view: TextView, date: DateTime?) {
        view.text = date?.format(formatter)
    }

    override fun bindTime(view: TextView, time: DateTime?) {
        view.text = time?.format(timeFormatter)
    }
}
