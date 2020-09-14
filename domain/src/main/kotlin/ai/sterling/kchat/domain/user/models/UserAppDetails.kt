package ai.sterling.kchat.domain.user.models

data class UserAppDetails(
    val appVersion: String,
    val osVersion: String,
    val deviceType: String,
    val timeZone: String
)
