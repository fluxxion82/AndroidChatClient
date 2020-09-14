package ai.sterling.kchat.domain.user.models

sealed class UserEvent {
    data class LoginChanged(val user: AppUser) : UserEvent()
    data class DetailsChanged(val details: ProfileDetails) : UserEvent()
}
