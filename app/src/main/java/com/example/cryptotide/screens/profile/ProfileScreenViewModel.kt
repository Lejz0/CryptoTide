package com.example.cryptotide.screens.profile

import com.example.cryptotide.CryptoTideAppViewModel
import com.example.cryptotide.model.service.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class ProfileScreenViewModel @Inject constructor(private val accountService: AccountService): CryptoTideAppViewModel(){

    fun onSignOutClick(onSuccessNavigation:() -> Unit) {
        launchCatching {
            accountService.signOut()
            onSuccessNavigation()
        }
    }

    fun onDeleteAccountClick(onSuccessNavigation:() -> Unit) {
        launchCatching {
            accountService.deleteAccount()
            onSuccessNavigation()
        }
    }
}