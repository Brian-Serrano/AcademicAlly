package com.serrano.academically.viewmodel

import com.serrano.academically.datastore.UpdateUserPref
import com.serrano.academically.room.UserRepository
import com.serrano.academically.utils.LoginInput
import com.serrano.academically.utils.ValidationMessage
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {

    private val _loginInput = MutableStateFlow(LoginInput("", "", "", false))
    val loginInput: StateFlow<LoginInput> = _loginInput.asStateFlow()

    fun updateInput(newLoginInput: LoginInput) {
        _loginInput.value = newLoginInput
    }

    fun validateUserLoginAsynchronously(context: Context, role: String, li: LoginInput, navigate: (String) -> Unit, error: (String) -> Unit) {
        viewModelScope.launch {
            try {
                // Validate and login user
                val vm = validateUserLogin(role, li)
                if (vm.isValid) {
                    UpdateUserPref.updateDataByLoggingIn(context, li.remember, vm.message.toInt(), li.email, li.password)
                    updateInput(loginInput.value.copy(email = "", password = "", error = ""))
                    navigate("Dashboard/${vm.message}")
                }
                else error(vm.message)
            }
            catch (e: Exception) {
                error("Something went wrong processing your credentials.")
            }
        }
    }

    private suspend fun validateUserLogin(role: String, li: LoginInput): ValidationMessage {
        val id = userRepository.getUserId(li.email, li.password, role).firstOrNull() ?: 0
        return when {
            li.email.isEmpty() || li.password.isEmpty() -> ValidationMessage(false, "Fill up all empty fields")
            li.email.length < 15 || li.email.length > 40 || li.password.length < 8 || li.password.length > 20 -> ValidationMessage(false, "Fill up fields with specified length")
            id != 0 -> ValidationMessage(true, id.toString())
            else -> ValidationMessage(false, "User not found")
        }
    }
}