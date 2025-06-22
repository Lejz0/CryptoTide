package com.example.cryptotide.screens.profile

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.cryptotide.CryptoTideAppViewModel
import com.example.cryptotide.model.service.AccountService
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.tasks.await

@HiltViewModel
class ProfileScreenViewModel @Inject constructor(private val accountService: AccountService) :
    CryptoTideAppViewModel() {

    var profilePhotoUrl by mutableStateOf(Firebase.auth.currentUser?.photoUrl?.toString())
        private set

    fun onSignOutClick(onSuccessNavigation: () -> Unit) {
        launchCatching {
            accountService.signOut()
            onSuccessNavigation()
        }
    }

    fun onDeleteAccountClick(onSuccessNavigation: () -> Unit) {
        launchCatching {
            accountService.deleteAccount()
            onSuccessNavigation()
        }
    }

    fun uploadImageAndUpdateProfile(uri : Uri) {
        isLoading = true
        launchCatching {
            accountService.uploadProfileImage(uri)
            refreshUserProfile()
            isLoading = false
        }
    }

    suspend fun refreshUserProfile() {
        val refreshedUser = Firebase.auth.currentUser
        refreshedUser?.reload()?.await()
        profilePhotoUrl = refreshedUser?.photoUrl?.toString()
    }
}