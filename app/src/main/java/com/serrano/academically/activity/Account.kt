package com.serrano.academically.activity

import com.serrano.academically.custom_composables.BlackButton
import com.serrano.academically.custom_composables.DrawerAndScaffold
import com.serrano.academically.custom_composables.ErrorComposable
import com.serrano.academically.custom_composables.Loading
import com.serrano.academically.custom_composables.LoginTextField
import com.serrano.academically.custom_composables.Text_1
import com.serrano.academically.custom_composables.YellowCard
import com.serrano.academically.ui.theme.Strings
import com.serrano.academically.utils.ManageAccountFields
import com.serrano.academically.utils.PasswordFields
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.utils.UserDrawerData
import com.serrano.academically.viewmodel.AccountViewModel
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope

@Composable
fun Account(
    scope: CoroutineScope,
    drawerState: DrawerState,
    userId: Int,
    navController: NavController,
    context: Context,
    tabs: List<String> = listOf("Info", "Password", "More"),
    accountViewModel: AccountViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        accountViewModel.getData(userId)
    }

    val user by accountViewModel.userData.collectAsState()
    val process by accountViewModel.processState.collectAsState()
    val accountFields by accountViewModel.accountFields.collectAsState()
    val passwordFields by accountViewModel.passwordFields.collectAsState()
    val tabIndex by accountViewModel.tabIndex.collectAsState()

    when (process) {
        ProcessState.Error -> ErrorComposable(navController)
        ProcessState.Loading -> Loading()
        ProcessState.Success -> {
            DrawerAndScaffold(
                scope = scope,
                drawerState = drawerState,
                user = UserDrawerData(user.id, user.name, user.role, user.email, user.degree),
                topBarText = Strings.manageAccount,
                navController = navController,
                context = context
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                        .background(MaterialTheme.colorScheme.primary)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TabRow(
                        selectedTabIndex = tabIndex,
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = Color.Black
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                text = {
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                },
                                selected = tabIndex == index,
                                onClick = { accountViewModel.updateTabIndex(index) }
                            )
                        }
                    }
                    YellowCard(MaterialTheme.colorScheme.tertiary) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(25.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(15.dp)
                        ) {
                            when (tabIndex) {
                                0 -> Info(user.id, accountFields, accountViewModel)
                                1 -> Password(user.id, user.password, passwordFields, accountViewModel)
                                2 -> More {
                                    try {
                                        accountViewModel.switchRole(user.role, user.id)
                                        navController.navigate("Dashboard/${user.id}")
                                    }
                                    catch (e: Exception) {
                                        Toast.makeText(context, "Failed to switch role", Toast.LENGTH_LONG).show()
                                        navController.navigate("Dashboard/${user.id}")
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
    id: Int,
    accountFields: ManageAccountFields,
    accountViewModel: AccountViewModel
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Icon(
            imageVector = Icons.Filled.AccountCircle,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(100.dp)
        )
        BlackButton(
            text = Strings.upload,
            action = {}
        )
    }
    Text_1(text = "Name")
    LoginTextField(
        inputName = "Name",
        input = accountFields.name,
        onInputChange = { accountViewModel.updateAccountFields(accountFields.copy(name = it)) },
        supportingText = "Should be 5-20 characters"
    )
    Text_1(text = "Degree")
    LoginTextField(
        inputName = "Degree",
        input = accountFields.degree,
        onInputChange = { accountViewModel.updateAccountFields(accountFields.copy(degree = it)) },
        supportingText = "Should be acronyms of degrees available in STI"
    )
    Text_1(text = "Age")
    LoginTextField(
        inputName = "Age",
        input = accountFields.age,
        onInputChange = { accountViewModel.updateAccountFields(accountFields.copy(age = it)) },
        supportingText = "Should range between 15-50"
    )
    Text_1(text = "Address")
    LoginTextField(
        inputName = "Address",
        input = accountFields.address,
        onInputChange = { accountViewModel.updateAccountFields(accountFields.copy(address = it)) },
        supportingText = "Should be 15-40 characters"
    )
    Text_1(text = "Contact Number")
    LoginTextField(
        inputName = "Contact Number",
        input = accountFields.contactNumber,
        onInputChange = { accountViewModel.updateAccountFields(accountFields.copy(contactNumber = it)) },
        supportingText = "Should be 10-100 characters"
    )
    Text_1(text = "Summary")
    LoginTextField(
        inputName = "Summary",
        input = accountFields.summary,
        onInputChange = { accountViewModel.updateAccountFields(accountFields.copy(summary = it)) },
        singleLine = false,
        minLines = 3,
        maxLines = 5,
        supportingText = "Should be 30-200 characters"
    )
    Text_1(text = "Educational Background")
    LoginTextField(
        inputName = "Educational Background",
        input = accountFields.educationalBackground,
        onInputChange = { accountViewModel.updateAccountFields(accountFields.copy(educationalBackground = it)) },
        singleLine = false,
        minLines = 3,
        maxLines = 5,
        supportingText = "Should be 30-200 characters"
    )
    BlackButton(
        text = Strings.save,
        action = {
            accountViewModel.saveInfo(
                id = id,
                accountFields = accountFields,
                showMessage = { accountViewModel.updateAccountFields(accountFields.copy(error = it.message, errorColor = if (it.isValid) Color.Green else Color.Red)) }
            )
        }
    )
    Text(
        text = accountFields.error,
        color = accountFields.errorColor,
        style = MaterialTheme.typography.bodyMedium
    )
}

@Composable
fun Password(
    id: Int,
    currentPassword: String,
    passwordFields: PasswordFields,
    accountViewModel: AccountViewModel
) {
    Text_1(text = "Current Password")
    LoginTextField(
        inputName = "Current Password",
        input = passwordFields.currentPassword,
        onInputChange = { accountViewModel.updatePasswordFields(passwordFields.copy(currentPassword = it)) },
        visualTransformation = PasswordVisualTransformation()
    )
    Text_1(text = "New Password")
    LoginTextField(
        inputName = "New Password",
        input = passwordFields.newPassword,
        onInputChange = { accountViewModel.updatePasswordFields(passwordFields.copy(newPassword = it)) },
        visualTransformation = PasswordVisualTransformation(),
        supportingText = "Should have at least one letter and number, and 8-20 characters"
    )
    Text_1(text = "Confirm Password")
    LoginTextField(
        inputName = "Confirm Password",
        input = passwordFields.confirmPassword,
        onInputChange = { accountViewModel.updatePasswordFields(passwordFields.copy(confirmPassword = it)) },
        visualTransformation = PasswordVisualTransformation()
    )
    BlackButton(
        text = Strings.save,
        action = {
            accountViewModel.savePassword(
                id = id,
                currentPassword = currentPassword,
                passwordFields = passwordFields,
                showMessage = { accountViewModel.updatePasswordFields(passwordFields.copy(error = it.message, errorColor = if (it.isValid) Color.Green else Color.Red)) }
            )
        }
    )
    Text(
        text = passwordFields.error,
        color = passwordFields.errorColor,
        style = MaterialTheme.typography.bodyMedium
    )
}

@Composable
fun More(
    action: () -> Unit
) {
    BlackButton(
        text = Strings.switchRole,
        action = action
    )
}