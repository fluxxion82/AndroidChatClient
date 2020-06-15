package ai.sterling.kchat.android.ui.chat.di

import ai.sterling.kchat.android.di.ViewModelKey
import ai.sterling.kchat.android.ui.chat.ChatViewModel
import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ChatModule {

    @Binds
    @IntoMap
    @ViewModelKey(ChatViewModel::class)
    abstract fun viewModel(viewModel: ChatViewModel): ViewModel
}