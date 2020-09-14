package ai.sterling.kchat.domain.user.validators

interface UsernameValidator {
    fun validate(username: String): Boolean
}
