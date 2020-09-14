package ai.sterling.kchat.domain.user.models

sealed class AppUser {
    object Anonymous : AppUser()
    data class LoggedIn(
        val id: Long,
        val username: String,
        val createdAt: Long
    ) : AppUser()
}
