package ai.sterling.kchat.android.ui.settings

import ai.sterling.kchat.R
import ai.sterling.kchat.android.ui.settings.models.ServerInfoNavigation
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EnterSettingsFragment : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    fun initView(savedInstanceState: Bundle?) {
    }
}