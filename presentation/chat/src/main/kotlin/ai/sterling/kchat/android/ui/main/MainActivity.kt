package ai.sterling.kchat.android.ui.main

import ai.sterling.kchat.R
import ai.sterling.kchat.domain.app.ExitApp
import ai.sterling.kchat.domain.base.invoke
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class MainActivity : AppCompatActivity(), HasAndroidInjector {
    private lateinit var mNavController: NavController

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    @Inject
    lateinit var exitApp: ExitApp

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        mNavController = findNavController(R.id.nav_host_fragment)
    }

    override fun onStop() {
        runBlocking {
            launch {
                exitApp()
            }
        }

        super.onStop()
    }

    override fun androidInjector(): AndroidInjector<Any> = androidInjector
}
