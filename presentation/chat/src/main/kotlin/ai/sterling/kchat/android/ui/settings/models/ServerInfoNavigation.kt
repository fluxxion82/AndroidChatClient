package ai.sterling.kchat.android.ui.settings.models

sealed class ServerInfoNavigation {
    object UpdateSucceeded : ServerInfoNavigation()
    object UpdateError : ServerInfoNavigation()
    object NoInternetError : ServerInfoNavigation()
}
