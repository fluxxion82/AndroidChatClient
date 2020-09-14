package ai.sterling.kchat.domain.initialization.models

sealed class InitialAppState {
    object ObsoleteVersionInstalled : InitialAppState()
}

sealed class UserState : InitialAppState() {
    object Anonymous : UserState()
    object LoggedIn : UserState()
}
