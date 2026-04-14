package com.example.tradetrack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    
    private val _currentUser = MutableStateFlow(auth.currentUser)
    val currentUser = _currentUser.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun login(email: String, password: String, onLoginSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            _error.value = "Please fill in all fields"
            return
        }
        
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                _currentUser.value = auth.currentUser
                onLoginSuccess()
            } catch (e: Exception) {
                _error.value = e.message ?: "Login failed"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signUp(name: String, email: String, password: String, onLoginSuccess: () -> Unit) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            _error.value = "Please fill in all fields"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val user = result.user
                
                // Update profile with name
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()
                user?.updateProfile(profileUpdates)?.await()

                _currentUser.value = auth.currentUser
                onLoginSuccess()
            } catch (e: Exception) {
                _error.value = e.message ?: "Sign up failed"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout(onLogout: () -> Unit) {
        auth.signOut()
        _currentUser.value = null
        onLogout()
    }
}
