package com.example.cryptotide.model.service

import com.example.cryptotide.model.User
import kotlinx.coroutines.flow.Flow

interface AccountService  {
    val currentUser: Flow<User?>
    fun hasUser(): Boolean
    suspend fun signIn(email: String, password: String)
    suspend fun signUp(email: String, password: String)
    suspend fun signOut()
}