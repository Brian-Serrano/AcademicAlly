package thesis.academic.ally.viewmodel

import android.app.Application
import android.net.Uri
import android.widget.Toast
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import thesis.academic.ally.api.AcademicallyApi
import thesis.academic.ally.api.Info
import thesis.academic.ally.api.InfoBody
import thesis.academic.ally.api.NoCurrentUser
import thesis.academic.ally.api.PasswordBody
import thesis.academic.ally.api.Validation
import thesis.academic.ally.datastore.UserCacheRepository
import thesis.academic.ally.utils.AccountDialogState
import thesis.academic.ally.utils.ActivityCacheManager
import thesis.academic.ally.utils.ManageAccountFields
import thesis.academic.ally.utils.PasswordFields
import thesis.academic.ally.utils.ProcessState
import thesis.academic.ally.utils.Utils
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val academicallyApi: AcademicallyApi,
    private val userCacheRepository: UserCacheRepository,
    application: Application
) : BaseViewModel(application) {

    private val _userData = MutableStateFlow(Info())
    val userData: StateFlow<Info> = _userData.asStateFlow()

    private val _accountFields = MutableStateFlow(ManageAccountFields())
    val accountFields: StateFlow<ManageAccountFields> = _accountFields.asStateFlow()

    private val _passwordFields = MutableStateFlow(PasswordFields())
    val passwordFields: StateFlow<PasswordFields> = _passwordFields.asStateFlow()

    private val _tabIndex = MutableStateFlow(0)
    val tabIndex: StateFlow<Int> = _tabIndex.asStateFlow()

    private val _buttonsEnabled = MutableStateFlow(listOf(true, true, true, true))
    val buttonsEnabled: StateFlow<List<Boolean>> = _buttonsEnabled.asStateFlow()

    private val _selectedImage = MutableStateFlow(ImageBitmap(100, 100))
    val selectedImage: StateFlow<ImageBitmap> = _selectedImage.asStateFlow()

    private val _isRefreshLoading = MutableStateFlow(false)
    val isRefreshLoading: StateFlow<Boolean> = _isRefreshLoading.asStateFlow()

    private val _accountDialogState = MutableStateFlow(AccountDialogState())
    val accountDialogState: StateFlow<AccountDialogState> = _accountDialogState.asStateFlow()

    fun getData() {
        viewModelScope.launch {
            try {
                val accountCache = ActivityCacheManager.account

                if (accountCache != null) {
                    _userData.value = accountCache
                } else {
                    callApi()
                }

                refreshFields()

                mutableProcessState.value = ProcessState.Success
            } catch (e: Exception) {
                mutableProcessState.value = ProcessState.Error(e.message ?: "")
            }
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            try {
                _isRefreshLoading.value = true

                callApi()

                refreshFields()

                _isRefreshLoading.value = false

                mutableProcessState.value = ProcessState.Success
            } catch (e: Exception) {
                _isRefreshLoading.value = false
                Toast.makeText(getApplication(), "Failed to refresh data.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun refreshFields() {
        _accountFields.value = ManageAccountFields(
            _userData.value.name,
            _userData.value.degree,
            _userData.value.age.toString(),
            _userData.value.address,
            _userData.value.contactNumber,
            _userData.value.summary,
            _userData.value.educationalBackground,
            Utils.stringToLocalTime(_userData.value.freeTutoringTime),
            "",
            false
        )

        _selectedImage.value = Utils.convertToImage(_userData.value.image)
    }

    private suspend fun callApi() {
        Utils.checkAuthentication(getApplication(), userCacheRepository, academicallyApi)

        val response = when (val userData = academicallyApi.getInfo()) {
            is NoCurrentUser.Success -> userData
            is NoCurrentUser.Error -> throw IllegalArgumentException(userData.error)
        }
        _userData.value = response.data!!
        ActivityCacheManager.account = response.data
    }

    fun selectImage(uri: Uri?) {
        if (uri != null) {
            _selectedImage.value = Utils.convertToImage(uri, getApplication())
        }
    }

    private fun toggleButtons(index: Int, value: Boolean) {
        _buttonsEnabled.value = _buttonsEnabled.value.mapIndexed { idx, item -> if (index == idx) value else item }
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

    fun updateDialogState(newState: AccountDialogState) {
        _accountDialogState.value = newState
    }

    fun saveInfo(accountFields: ManageAccountFields, showMessage: (Validation) -> Unit) {
        viewModelScope.launch {
            try {
                toggleButtons(0, false)

                Utils.checkAuthentication(getApplication(), userCacheRepository, academicallyApi)

                val response = academicallyApi.updateInfo(
                    InfoBody(
                        name = accountFields.name,
                        age = accountFields.age.toInt(),
                        degree = accountFields.degree,
                        address = accountFields.address,
                        contactNumber = accountFields.contactNumber,
                        summary = accountFields.summary,
                        educationalBackground = accountFields.educationalBackground,
                        freeTutoringTime = Utils.localTimeToString(accountFields.freeTutoringTime)
                    )
                )
                val validation = when (response) {
                    is NoCurrentUser.Success -> response.data!!
                    is NoCurrentUser.Error -> throw IllegalArgumentException(response.error)
                }

                ActivityCacheManager.account = null
                ActivityCacheManager.currentUser = null

                toggleButtons(0, true)

                showMessage(validation)
            } catch (e: Exception) {
                toggleButtons(0, true)
                showMessage(Validation(false, e.message ?: ""))
            }
        }
    }

    fun savePassword(passwordFields: PasswordFields, showMessage: (Validation) -> Unit) {
        viewModelScope.launch {
            try {
                toggleButtons(1, false)

                Utils.checkAuthentication(getApplication(), userCacheRepository, academicallyApi)

                val response = academicallyApi.updatePassword(
                    PasswordBody(
                        passwordFields.currentPassword,
                        passwordFields.newPassword,
                        passwordFields.confirmPassword
                    )
                )
                val validation = when (response) {
                    is NoCurrentUser.Success -> response.data!!
                    is NoCurrentUser.Error -> throw IllegalArgumentException(response.error)
                }

                userCacheRepository.updatePassword(passwordFields.newPassword)

                toggleButtons(1, true)

                showMessage(validation)
            } catch (e: Exception) {
                toggleButtons(1, true)
                showMessage(Validation(false, e.message ?: ""))
            }
        }
    }

    fun switchRole(newRole: String, navigate: (String) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                toggleButtons(2, false)

                Utils.checkAuthentication(getApplication(), userCacheRepository, academicallyApi)

                val response = when (val userData = academicallyApi.switchRole()) {
                    is NoCurrentUser.Success -> userData.data!!
                    is NoCurrentUser.Error -> throw IllegalArgumentException(userData.error)
                }

                if (response.isValid) {
                    userCacheRepository.updateRole(newRole)
                    ActivityCacheManager.clearCache()
                    toggleButtons(2, true)
                    navigate(response.message)
                } else {
                    toggleButtons(2, true)
                    onError(response.message)
                }
            } catch (e: Exception) {
                toggleButtons(2, true)
                navigate("Failed to switch role")
            }
        }
    }

    fun uploadImage(imageBitmap: ImageBitmap, showMessage: (Validation) -> Unit) {
        viewModelScope.launch {
            try {
                toggleButtons(3, false)

                Utils.checkAuthentication(getApplication(), userCacheRepository, academicallyApi)

                val file = Utils.bitmapToFile(imageBitmap, getApplication())
                val imagePart = MultipartBody.Part.createFormData("file", file.name, file.asRequestBody("image/*".toMediaTypeOrNull()))
                academicallyApi.uploadImage(imagePart)
                if (file.exists()) file.delete()

                ActivityCacheManager.account = null

                toggleButtons(3, true)

                showMessage(Validation(true, "Image Uploaded"))
            } catch (e: Exception) {
                toggleButtons(3, true)
                showMessage(Validation(false, "Cannot Upload Image"))
            }
        }
    }
}