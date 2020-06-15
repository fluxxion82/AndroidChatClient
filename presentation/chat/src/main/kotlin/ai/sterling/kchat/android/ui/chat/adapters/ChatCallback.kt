package ai.sterling.kchat.android.ui.chat.adapters

import ai.sterling.kchat.domain.chat.model.ChatMessage
import ai.sterling.kchat.domain.chat.model.Message
import androidx.recyclerview.widget.DiffUtil

internal class ChatCallback(
    private val old: List<ChatMessage>,
    private val new: List<ChatMessage>
) : DiffUtil.Callback() {

    override fun getOldListSize() = old.size + 1

    override fun getNewListSize() = new.size + 1

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        if (oldItemPosition == 0 || newItemPosition == 0) {
            oldItemPosition == newItemPosition
        } else {
            old[oldItemPosition - 1].id == new[newItemPosition - 1].id
        }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        if (oldItemPosition == 0 || newItemPosition == 0) {
            oldItemPosition == newItemPosition
        } else {
            old[oldItemPosition - 1] == new[newItemPosition - 1]
        }
}
