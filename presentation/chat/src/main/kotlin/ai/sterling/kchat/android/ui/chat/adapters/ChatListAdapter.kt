package ai.sterling.kchat.android.ui.chat.adapters

import ai.sterling.kchat.BR
import ai.sterling.kchat.android.ui.base.DefaultHolder
import ai.sterling.kchat.android.ui.chat.ChatViewModel
import ai.sterling.kchat.databinding.ChatMeBinding
import ai.sterling.kchat.databinding.ChatReplyBinding
import ai.sterling.kchat.databinding.ChatReplyOptionBinding
import ai.sterling.kchat.domain.chat.model.ChatMessage
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.observe
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

internal class ChatListAdapter(
    lifecycleOwner: LifecycleOwner,
    private val viewModel: ChatViewModel
) : RecyclerView.Adapter<DefaultHolder<ViewDataBinding>>() {

    private var oldList: MutableList<ChatMessage> = mutableListOf()

    init {
        viewModel.messages.observe(lifecycleOwner) {
            DiffUtil.calculateDiff(ChatCallback(oldList, it.orEmpty())).dispatchUpdatesTo(this)
            oldList.addAll(it.orEmpty())
        }
    }

    override fun getItemId(position: Int) =
        viewModel.messages.value.orEmpty()[position].id.hashCode().toLong()


    override fun getItemViewType(position: Int) =
        when (viewModel.messages.value.orEmpty()[position].type) {
            ChatMessage.REPLY -> {
                ITEM_TYPE_REPLY
            }
            ChatMessage.MESSAGE -> {
                ITEM_TYPE_MESSAGE
            }
            else -> ITEM_TYPE_NEUTRAL
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DefaultHolder<ViewDataBinding> {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            ITEM_TYPE_MESSAGE -> ChatMeBinding.inflate(inflater, parent, false)
            ITEM_TYPE_REPLY -> ChatReplyBinding.inflate(inflater, parent, false)
            ITEM_TYPE_NEUTRAL -> ChatReplyOptionBinding.inflate(inflater, parent, false)
            else -> throw IllegalArgumentException("Unsupported type")
        }.let(::DefaultHolder)
    }

    override fun getItemCount(): Int = viewModel.messages.value.orEmpty().count()

    override fun onBindViewHolder(holder: DefaultHolder<ViewDataBinding>, position: Int) {
        holder.binding.setVariable(
            BR.model,
            when (holder.itemViewType) {
                ITEM_TYPE_NEUTRAL, ITEM_TYPE_REPLY, ITEM_TYPE_MESSAGE -> viewModel.messages.value?.get(position)
                else -> throw IllegalArgumentException("Unsupported type")
            }
        )
        holder.binding.executePendingBindings()
    }

    companion object {
        private const val ITEM_TYPE_MESSAGE= 1
        private const val ITEM_TYPE_REPLY = 2
        private const val ITEM_TYPE_NEUTRAL = 3
    }
}
