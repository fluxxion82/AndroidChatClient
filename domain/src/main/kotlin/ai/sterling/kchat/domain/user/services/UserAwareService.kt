package ai.sterling.kchat.domain.user.services

import ai.sterling.kchat.domain.user.models.AppUser

interface UserAwareService {
    suspend fun onUserChanged(user: AppUser)
}
