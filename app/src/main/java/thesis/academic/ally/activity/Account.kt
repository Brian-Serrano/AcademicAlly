package thesis.academic.ally.activity

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
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
import kotlinx.coroutines.CoroutineScope
import thesis.academic.ally.api.DrawerData
import thesis.academic.ally.custom_composables.AvailabilityInput
import thesis.academic.ally.custom_composables.BlackButton
import thesis.academic.ally.custom_composables.CustomCard
import thesis.academic.ally.custom_composables.CustomInputField
import thesis.academic.ally.custom_composables.CustomTab
import thesis.academic.ally.custom_composables.DrawerAndScaffold
import thesis.academic.ally.custom_composables.ErrorComposable
import thesis.academic.ally.custom_composables.Loading
import thesis.academic.ally.custom_composables.ScaffoldNoDrawer
import thesis.academic.ally.custom_composables.TimePickerDialog
import thesis.academic.ally.utils.AccountDialogState
import thesis.academic.ally.utils.ManageAccountFields
import thesis.academic.ally.utils.PasswordFields
import thesis.academic.ally.utils.ProcessState
import thesis.academic.ally.utils.Routes
import thesis.academic.ally.viewmodel.AccountViewModel
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
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
        accountViewModel.getData()
    }

    val user by accountViewModel.userData.collectAsState()
    val process by accountViewModel.processState.collectAsState()
    val accountFields by accountViewModel.accountFields.collectAsState()
    val passwordFields by accountViewModel.passwordFields.collectAsState()
    val tabIndex by accountViewModel.tabIndex.collectAsState()
    val buttonsEnabled by accountViewModel.buttonsEnabled.collectAsState()
    val selectedImage by accountViewModel.selectedImage.collectAsState()
    val isRefreshLoading by accountViewModel.isRefreshLoading.collectAsState()
    val dialogState by accountViewModel.accountDialogState.collectAsState()

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshLoading)
    val onRefresh = { accountViewModel.refreshData() }

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
                selected = Routes.ACCOUNT
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(paddingValues)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
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
                                            0 -> Info(buttonsEnabled[0], buttonsEnabled[3], accountFields, selectedImage, accountViewModel)

                                            1 -> Password(buttonsEnabled[1], passwordFields, accountViewModel)

                                            2 -> More(buttonsEnabled[2]) {
                                                accountViewModel.switchRole(
                                                    newRole = if (user.role == "STUDENT") "TUTOR" else "STUDENT",
                                                    navigate = {
                                                        Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                                                        navController.navigate(Routes.DASHBOARD) {
                                                            popUpTo(navController.graph.id) {
                                                                inclusive = false
                                                            }
                                                        }
                                                    },
                                                    onError = {
                                                        Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                                                        navController.navigate(Routes.CHOOSE_ASSESSMENT)
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (dialogState.dialogOpen) {
                        val timePickerState = rememberTimePickerState(
                            initialHour = dialogState.time.hour,
                            initialMinute = dialogState.time.minute
                        )
                        TimePickerDialog(
                            onDismissRequest = {
                                accountViewModel.updateDialogState(dialogState.copy(dialogOpen = false))
                            },
                            confirmButton = {
                                BlackButton(
                                    text = "OK",
                                    action = {
                                        accountViewModel.updateDialogState(dialogState.copy(dialogOpen = false))
                                        accountViewModel.updateAccountFields(
                                            accountFields.copy(
                                                freeTutoringTime = accountFields.freeTutoringTime.mapIndexed { idx, time ->
                                                    if (idx == dialogState.day) {
                                                        if (dialogState.threshold == 0) {
                                                            time.copy(
                                                                from = LocalTime.of(timePickerState.hour, timePickerState.minute)
                                                            )
                                                        } else {
                                                            time.copy(
                                                                to = LocalTime.of(timePickerState.hour, timePickerState.minute)
                                                            )
                                                        }
                                                    } else time
                                                }
                                            )
                                        )
                                    }
                                )
                            }
                        ) {
                            Column(
                                modifier = Modifier
                                    .clip(MaterialTheme.shapes.extraSmall)
                                    .background(MaterialTheme.colorScheme.tertiary),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                TimePicker(
                                    state = timePickerState,
                                    colors = TimePickerDefaults.colors(
                                        clockDialColor = MaterialTheme.colorScheme.tertiaryContainer,
                                        selectorColor = MaterialTheme.colorScheme.surface,
                                        containerColor = MaterialTheme.colorScheme.tertiary,
                                        timeSelectorSelectedContainerColor = MaterialTheme.colorScheme.surface,
                                        timeSelectorSelectedContentColor = MaterialTheme.colorScheme.onBackground,
                                        timeSelectorUnselectedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                        timeSelectorUnselectedContentColor = MaterialTheme.colorScheme.onBackground
                                    )
                                )
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
    accountViewModel: AccountViewModel
) {
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { accountViewModel.selectImage(it) }
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
                        }
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
        text = "Tutoring Availability",
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier.padding(10.dp)
    )
    AvailabilityInput(
        dates = accountFields.freeTutoringTime,
        openDialog = { time, day, threshold ->
            accountViewModel.updateDialogState(
                AccountDialogState(
                    time = time,
                    day = day,
                    threshold = threshold,
                    dialogOpen = true
                )
            )
        },
        onNotAvailable = { day ->
            accountViewModel.updateAccountFields(
                accountFields.copy(
                    freeTutoringTime = accountFields.freeTutoringTime.mapIndexed { idx, data ->
                        if (idx == day) {
                            data.copy(
                                from = LocalTime.of(0, 0),
                                to = LocalTime.of(0, 0)
                            )
                        } else data
                    }
                )
            )
        }
    )
    BlackButton(
        text = "SAVE",
        action = {
            accountViewModel.saveInfo(
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