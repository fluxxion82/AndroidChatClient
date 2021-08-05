package ai.sterling.kchat.android.ui.main

import ai.sterling.kchat.R
import ai.sterling.kchat.domain.app.ExitApp
import ai.sterling.kchat.domain.base.invoke
import ai.sterling.kchat.android.ui.KChat
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
@ExperimentalComposeUiApi
@ExperimentalFoundationApi
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                MyApp()
            }
        }
    }

    @Composable
    fun MyApp() {
        Scaffold(
            content = {
                KChat()
            }
        )
    }
}

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Preview(name = "Main content")
@Composable
fun PreviewMain() {
    val navController = rememberNavController()
    KChat()
}
