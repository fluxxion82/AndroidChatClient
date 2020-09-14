package ai.sterling.kchat.domain.user.models

sealed class ChangedDetail {
    data class Username(val value: String) : ChangedDetail()
}
