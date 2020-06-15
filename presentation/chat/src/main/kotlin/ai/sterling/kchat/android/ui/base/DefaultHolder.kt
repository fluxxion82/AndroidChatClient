package ai.sterling.kchat.android.ui.base

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

internal class DefaultHolder<T : ViewDataBinding>(val binding: T) : RecyclerView.ViewHolder(binding.root)
