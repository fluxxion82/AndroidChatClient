package ai.sterling.kchat.android.ui.settings

import ai.sterling.kchat.R
import ai.sterling.kchat.android.ui.base.BaseDialogFragment
import ai.sterling.kchat.android.ui.settings.models.ServerInfoNavigation
import ai.sterling.kchat.databinding.FragmentUsernameDialogBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.observe
import androidx.navigation.NavDirections

class EnterSettingsFragment : BaseDialogFragment<FragmentUsernameDialogBinding, SettingsViewModel>() {
    override val viewModelClass = SettingsViewModel::class
    override val layoutId = R.layout.fragment_username_dialog

    override fun init(savedInstanceState: Bundle?) {
        viewModel.navigation.observe(this) { event ->
            event?.consume {
                when (it) {
                    ServerInfoNavigation.UPDATE_SUCCEEDED -> {
                        navController.navigate(R.id.chat_fragment) //EnterSettingsFragmentDirections.actionMyDialogToChatFragment()
                        dismiss()
                    }
                    ServerInfoNavigation.UPDATE_ERROR -> {
                        //Snackbar.make(binding.root, R.string.settings_configuration_save_error, Snackbar.LENGTH_SHORT).show()
                    }
                    ServerInfoNavigation.NO_INTERNET_ERROR -> {
                        //Snackbar.make(binding.root, R.string.no_internet, Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        dialog!!.window!!.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
        )
    }
}