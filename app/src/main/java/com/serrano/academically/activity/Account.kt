package com.serrano.academically.activity

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.serrano.academically.api.DrawerData
import com.serrano.academically.custom_composables.BlackButton
import com.serrano.academically.custom_composables.CustomTab
import com.serrano.academically.custom_composables.DrawerAndScaffold
import com.serrano.academically.custom_composables.ErrorComposable
import com.serrano.academically.custom_composables.Loading
import com.serrano.academically.custom_composables.CustomInputField
import com.serrano.academically.custom_composables.ScaffoldNoDrawer
import com.serrano.academically.custom_composables.CustomCard
import com.serrano.academically.utils.ManageAccountFields
import com.serrano.academically.utils.PasswordFields
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.viewmodel.AccountViewModel
import kotlinx.coroutines.CoroutineScope

@Composable
fun Account(
    scope: CoroutineScope,
    drawerState: DrawerState,
    navController: NavController,
    context: Context,
    tabs: List<String> = listOf("Info", "Password", "More"),
    accountViewModel: AccountViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        accountViewModel.getData(context)
    }

    val user by accountViewModel.userData.collectAsState()
    val process by accountViewModel.processState.collectAsState()
    val accountFields by accountViewModel.accountFields.collectAsState()
    val passwordFields by accountViewModel.passwordFields.collectAsState()
    val tabIndex by accountViewModel.tabIndex.collectAsState()
    val buttonsEnabled by accountViewModel.buttonsEnabled.collectAsState()
    val selectedImage by accountViewModel.selectedImage.collectAsState()
    val isRefreshLoading by accountViewModel.isRefreshLoading.collectAsState()

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshLoading)
    val onRefresh = { accountViewModel.refreshData(context) }

    when (val p = process) {
        is ProcessState.Error -> {
            ScaffoldNoDrawer(
                text = "Manage Account",
                navController = navController
            ) {
                ErrorComposable(navController, it, p.message, swipeRefreshState, onRefresh)
            }
        }

        is ProcessState.Loading -> {
            ScaffoldNoDrawer(
                text = "Manage Account",
                navController = navController
            ) {
                Loading(it)
            }
        }

        is ProcessState.Success -> {
            DrawerAndScaffold(
                scope = scope,
                drawerState = drawerState,
                user = DrawerData(user.id, user.name, user.role, user.email, user.degree),
                topBarText = "Manage Account",
                navController = navController,
                context = context,
                selected = "ManageAccounts"
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CustomTab(
                        tabIndex = tabIndex,
                        tabs = tabs,
                        onTabClick = { accountViewModel.updateTabIndex(it) }
                    )
                    SwipeRefresh(
                        state = swipeRefreshState,
                        onRefresh = onRefresh,
                        refreshTriggerDistance = 50.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.primary)
                                .verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CustomCard {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(25.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(15.dp)
                                ) {
                                    when (tabIndex) {
                                        0 -> Info(
                                            buttonsEnabled[0],
                                            buttonsEnabled[3],
                                            accountFields,
                                            selectedImage,
                                            context,
                                            accountViewModel
                                        )

                                        1 -> Password(
                                            context,
                                            buttonsEnabled[1],
                                            passwordFields,
                                            accountViewModel
                                        )

                                        2 -> More(buttonsEnabled[2]) {
                                            accountViewModel.switchRole(
                                                context = context,
                                                newRole = if (user.role == "STUDENT") "TUTOR" else "STUDENT",
                                                navigate = {
                                                    Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                                                    navController.navigate("Dashboard") {
                                                        popUpTo(navController.graph.id) {
                                                            inclusive = false
                                                        }
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Info(
    enabled: Boolean,
    uploadEnabled: Boolean,
    accountFields: ManageAccountFields,
    selectedImage: ImageBitmap,
    context: Context,
    accountViewModel: AccountViewModel
) {
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { accountViewModel.selectImage(it, context) }
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Image(
            bitmap = selectedImage,
            contentDescription = null,
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(50.dp))
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            BlackButton(
                text = "PICK IMAGE",
                action = {
                    imagePicker.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                }
            )
            BlackButton(
                text = "UPLOAD",
                action = {
                    accountViewModel.uploadImage(
                        imageBitmap = selectedImage,
                        showMessage = {
                            accountViewModel.updateAccountFields(
                                accountFields.copy(
                                    errorMessage = it.message,
                                    isError = it.isValid
                                )
                            )
                        },
                        context = context
                    )
                },
                enabled = uploadEnabled
            )
        }
    }
    Text(
        text = "Name",
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier.padding(10.dp)
    )
    CustomInputField(
        inputName = "Name",
        input = accountFields.name,
        onInputChange = { accountViewModel.updateAccountFields(accountFields.copy(name = it)) },
        supportingText = "Should be 5-20 characters"
    )
    Text(
        text = "Program",
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier.padding(10.dp)
    )
    CustomInputField(
        inputName = "Program",
        input = accountFields.degree,
        onInputChange = { accountViewModel.updateAccountFields(accountFields.copy(degree = it)) },
        supportingText = "Should be acronyms of programs available in STI"
    )
    Text(
        text = "Age",
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier.padding(10.dp)
    )
    CustomInputField(
        inputName = "Age",
        input = accountFields.age,
        onInputChange = { accountViewModel.updateAccountFields(accountFields.copy(age = it)) },
        supportingText = "Should range between 15-50"
    )
    Text(
        text = "Address",
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier.padding(10.dp)
    )
    CustomInputField(
        inputName = "Address",
        input = accountFields.address,
        onInputChange = { accountViewModel.updateAccountFields(accountFields.copy(address = it)) },
        supportingText = "Should be 15-40 characters"
    )
    Text(
        text = "Contact Number",
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier.padding(10.dp)
    )
    CustomInputField(
        inputName = "Contact Number",
        input = accountFields.contactNumber,
        onInputChange = { accountViewModel.updateAccountFields(accountFields.copy(contactNumber = it)) },
        supportingText = "Should be 10-100 characters"
    )
    Text(
        text = "Summary",
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier.padding(10.dp)
    )
    CustomInputField(
        inputName = "Summary",
        input = accountFields.summary,
        onInputChange = { accountViewModel.updateAccountFields(accountFields.copy(summary = it)) },
        singleLine = false,
        minLines = 3,
        maxLines = 5,
        supportingText = "Should be 30-200 characters"
    )
    Text(
        text = "Educational Background",
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier.padding(10.dp)
    )
    CustomInputField(
        inputName = "Educational Background",
        input = accountFields.educationalBackground,
        onInputChange = {
            accountViewModel.updateAccountFields(
                accountFields.copy(
                    educationalBackground = it
                )
            )
        },
        singleLine = false,
        minLines = 3,
        maxLines = 5,
        supportingText = "Should be 30-200 characters"
    )
    Text(
        text = "Free Tutoring Time",
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier.padding(10.dp)
    )
    CustomInputField(
        inputName = "Free Tutoring Time",
        input = accountFields.freeTutoringTime,
        onInputChange = {
            accountViewModel.updateAccountFields(
                accountFields.copy(
                    freeTutoringTime = it
                )
            )
        },
        singleLine = false,
        minLines = 3,
        maxLines = 5,
        supportingText = "Should be 15-100 characters"
    )
    BlackButton(
        text = "SAVE",
        action = {
            accountViewModel.saveInfo(
                context = context,
                accountFields = accountFields,
                showMessage = {
                    accountViewModel.updateAccountFields(
                        accountFields.copy(
                            errorMessage = it.message,
                            isError = it.isValid
                        )
                    )
                }
            )
        },
        enabled = enabled
    )
    Text(
        text = accountFields.errorMessage,
        color = if (accountFields.isError) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodyMedium
    )
}

@Composable
fun Password(
    context: Context,
    enabled: Boolean,
    passwordFields: PasswordFields,
    accountViewModel: AccountViewModel
) {
    Text(
        text = "Current Password",
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier.padding(10.dp)
    )
    CustomInputField(
        inputName = "Current Password",
        input = passwordFields.currentPassword,
        onInputChange = { accountViewModel.updatePasswordFields(passwordFields.copy(currentPassword = it)) },
        visualTransformation = PasswordVisualTransformation()
    )
    Text(
        text = "New Password",
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier.padding(10.dp)
    )
    CustomInputField(
        inputName = "New Password",
        input = passwordFields.newPassword,
        onInputChange = { accountViewModel.updatePasswordFields(passwordFields.copy(newPassword = it)) },
        visualTransformation = PasswordVisualTransformation(),
        supportingText = "Should have at least one letter and number, and 8-20 characters"
    )
    Text(
        text = "Confirm Password",
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier.padding(10.dp)
    )
    CustomInputField(
        inputName = "Confirm Password",
        input = passwordFields.confirmPassword,
        onInputChange = { accountViewModel.updatePasswordFields(passwordFields.copy(confirmPassword = it)) },
        visualTransformation = PasswordVisualTransformation()
    )
    BlackButton(
        text = "SAVE",
        action = {
            accountViewModel.savePassword(
                context = context,
                passwordFields = passwordFields,
                showMessage = {
                    accountViewModel.updatePasswordFields(
                        passwordFields.copy(
                            errorMessage = it.message,
                            isError = it.isValid
                        )
                    )
                }
            )
        },
        enabled = enabled
    )
    Text(
        text = passwordFields.errorMessage,
        color = if (passwordFields.isError) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodyMedium
    )
}

@Composable
fun More(
    enabled: Boolean,
    action: () -> Unit
) {
    BlackButton(
        text = "SWITCH ROLE",
        action = action,
        enabled = enabled
    )
}