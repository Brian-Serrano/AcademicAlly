package com.serrano.academically.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serrano.academically.room.UserRepository
import com.serrano.academically.utils.ManageAccountFields
import com.serrano.academically.utils.PasswordFields
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.utils.UserInfoAndCredentials
import com.serrano.academically.utils.ValidationMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _processState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = _processState.asStateFlow()

    private val _userData = MutableStateFlow(UserInfoAndCredentials())
    val userData: StateFlow<UserInfoAndCredentials> = _userData.asStateFlow()

    private val _accountFields = MutableStateFlow(ManageAccountFields())
    val accountFields: StateFlow<ManageAccountFields> = _accountFields.asStateFlow()

    private val _passwordFields = MutableStateFlow(PasswordFields())
    val passwordFields: StateFlow<PasswordFields> = _passwordFields.asStateFlow()

    private val _tabIndex = MutableStateFlow(0)
    val tabIndex: StateFlow<Int> = _tabIndex.asStateFlow()

    private val _buttonsEnabled = MutableStateFlow(listOf(true, true, true))
    val buttonsEnabled: StateFlow<List<Boolean>> = _buttonsEnabled.asStateFlow()

    fun getData(id: Int) {
        viewModelScope.launch {
            try {
                // Fetch user information and credentials and place in text fields
                val info = userRepository.getUserInfoAndCredentials(id).first()
                _userData.value = info
                _accountFields.value = ManageAccountFields(
                    info.name,
                    info.degree,
                    info.age.toString(),
                    info.address,
                    info.contactNumber,
                    info.summary,
                    info.educationalBackground,
                    "",
                    Color.Red
                )

                _processState.value = ProcessState.Success
            } catch (e: Exception) {
                _processState.value = ProcessState.Error
            }
        }
    }

    private fun toggleButtons(index: Int, value: Boolean) {
        _buttonsEnabled.value =
            _buttonsEnabled.value.mapIndexed { idx, item -> if (index == idx) value else item }
    }

    fun updateTabIndex(index: Int) {
        _tabIndex.value = index
    }

    fun updateAccountFields(newAccountField: ManageAccountFields) {
        _accountFields.value = newAccountField
    }

    fun updatePasswordFields(newPasswordField: PasswordFields) {
        _passwordFields.value = newPasswordField
    }

    fun saveInfo(
        id: Int,
        accountFields: ManageAccountFields,
        showMessage: (ValidationMessage) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // Disable button temporarily
                toggleButtons(0, false)

                // Validate and update user information
                val result = validateUserInfo(accountFields, userRepository.getUserNames().first())
                if (result.isValid) {
                    userRepository.updateUserInfo(
                        name = accountFields.name,
                        age = accountFields.age.toInt(),
                        degree = accountFields.degree,
                        address = accountFields.address,
                        contactNumber = accountFields.contactNumber,
                        summary = accountFields.summary,
                        educationalBackground = accountFields.educationalBackground,
                        id = id
                    )
                }

                // Enable button again
                toggleButtons(0, true)

                showMessage(result)
            } catch (e: Exception) {
                toggleButtons(0, true)
                showMessage(ValidationMessage(false, "Something went wrong saving your info."))
            }
        }
    }

    private fun validateUserInfo(
        accountFields: ManageAccountFields,
        existingNames: List<String>
    ): ValidationMessage {
        return when {
            accountFields.age.isEmpty() ||
                    accountFields.degree.isEmpty() ||
                    accountFields.address.isEmpty() ||
                    accountFields.name.isEmpty() ||
                    accountFields.contactNumber.isEmpty() ||
                    accountFields.summary.isEmpty() ||
                    accountFields.educationalBackground.isEmpty() -> ValidationMessage(
                false,
                "Please fill up empty fields."
            )

            accountFields.address.length < 15 || accountFields.address.length > 40 -> ValidationMessage(
                false,
                "Address should be 15-40 characters length."
            )

            accountFields.name.length < 5 || accountFields.name.length > 20 -> ValidationMessage(
                false,
                "Name should be 5-20 characters length."
            )

            accountFields.contactNumber.length < 10 || accountFields.contactNumber.length > 100 -> ValidationMessage(
                false,
                "Contact Number should be 10-100 characters length."
            )

            accountFields.summary.length < 30 || accountFields.summary.length > 200 -> ValidationMessage(
                false,
                "Summary should be 30-200 characters length."
            )

            accountFields.educationalBackground.length < 30 || accountFields.educationalBackground.length > 200 -> ValidationMessage(
                false,
                "Educational Background should be 30-200 characters length."
            )

            !accountFields.age.isDigitsOnly() -> ValidationMessage(false, "Age should be a number.")
            accountFields.age.toInt() < 15 || accountFields.age.toInt() > 50 -> ValidationMessage(
                false,
                "Age should range from 15 to 50"
            )

            !Regex("BSCS|HRS|STEM|IT|ACT|HRM|ABM").matches(accountFields.degree.uppercase()) -> ValidationMessage(
                false,
                "Please enter valid degree."
            )

            existingNames.any { it == accountFields.name } && accountFields.name != userData.value.name -> ValidationMessage(
                false,
                "Username already exists."
            )

            else -> ValidationMessage(true, "User Information Successfully Saved")
        }
    }

    fun savePassword(
        id: Int,
        currentPassword: String,
        passwordFields: PasswordFields,
        showMessage: (ValidationMessage) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // Disable button temporarily
                toggleButtons(1, false)

                // Validate and update user password
                val result = validatePassword(currentPassword, passwordFields)
                if (result.isValid) {
                    userRepository.updateUserPassword(passwordFields.newPassword, id)
                }

                // Enable button again
                toggleButtons(1, true)

                showMessage(result)
            } catch (e: Exception) {
                toggleButtons(1, true)
                showMessage(
                    ValidationMessage(
                        false,
                        "Something went wrong saving your new password."
                    )
                )
            }
        }
    }

    private fun validatePassword(
        currentPassword: String,
        passwordFields: PasswordFields
    ): ValidationMessage {
        return when {
            passwordFields.currentPassword.isEmpty() || passwordFields.newPassword.isEmpty() || passwordFields.confirmPassword.isEmpty() -> ValidationMessage(
                false,
                "Please fill up empty fields."
            )

            passwordFields.currentPassword != currentPassword -> ValidationMessage(
                false,
                "Current password do not match."
            )

            !Regex("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}\$").containsMatchIn(passwordFields.newPassword) -> ValidationMessage(
                false,
                "Invalid New Password."
            )

            passwordFields.newPassword != passwordFields.confirmPassword -> ValidationMessage(
                false,
                "New password do not match."
            )

            else -> ValidationMessage(true, "Password Successfully Saved")
        }
    }

    fun switchRole(role: String, id: Int, navigate: (String) -> Unit) {
        viewModelScope.launch {
            try {
                // Disable button temporarily
                toggleButtons(2, false)

                // Switch user role
                userRepository.updateUserRole(if (role == "STUDENT") "TUTOR" else "STUDENT", id)

                // Enable button again
                toggleButtons(2, true)

                navigate("Switch role successful!")
            } catch (e: Exception) {
                toggleButtons(2, true)
                navigate("Failed to switch role")
            }
        }
    }
}