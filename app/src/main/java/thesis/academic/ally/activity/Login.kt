package thesis.academic.ally.activity

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import thesis.academic.ally.R
import thesis.academic.ally.custom_composables.BlackButton
import thesis.academic.ally.custom_composables.DiagonalBackground
import thesis.academic.ally.custom_composables.CustomInputField
import thesis.academic.ally.datastore.UserCache
import thesis.academic.ally.utils.Routes
import thesis.academic.ally.viewmodel.LoginViewModel

@Composable
fun Login(
    navController: NavController,
    user: String,
    context: Context,
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    val loginInput by loginViewModel.loginInput.collectAsState()
    val enabled by loginViewModel.buttonEnabled.collectAsState()
    val forgotClickable by loginViewModel.forgotClickable.collectAsState()
    val userCache by context.userDataStore.data.collectAsState(initial = UserCache())

    LaunchedEffect(Unit) {
        if (userCache.isRemember) {
            loginViewModel.updateInput(
                loginInput.copy(
                    email = userCache.email,
                    password = userCache.password,
                    remember = true
                )
            )
        }
    }

    SelectionContainer {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(DiagonalBackground())
                    .background(MaterialTheme.colorScheme.secondary)
            )
            Image(
                painter = painterResource(id = R.drawable.sign_in_student),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            )
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(20.dp)
                    .clickable { navController.popBackStack() }
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = "Sign in as $user",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleMedium
                )
                CustomInputField(
                    inputName = "Email",
                    input = loginInput.email,
                    onInputChange = { loginViewModel.updateInput(loginInput.copy(email = it)) },
                    modifier = Modifier.padding(horizontal = 30.dp)
                )
                CustomInputField(
                    inputName = "Password",
                    input = loginInput.password,
                    onInputChange = { loginViewModel.updateInput(loginInput.copy(password = it)) },
                    visualTransformation = if (loginInput.passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.padding(horizontal = 30.dp),
                    trailingIcon = {
                        IconButton(onClick = { loginViewModel.updateInput(loginInput.copy(passwordVisibility = !loginInput.passwordVisibility)) }) {
                            Icon(
                                if (loginInput.passwordVisibility) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null,
                                tint = Color.Black
                            )
                        }
                    }
                )
                Text(
                    text = loginInput.error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(30.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = loginInput.remember,
                            onClick = { loginViewModel.updateInput(loginInput.copy(remember = !loginInput.remember)) }
                        )
                        Text(
                            text = "Remember Me",
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "Forgot Password",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            if (forgotClickable) {
                                loginViewModel.forgotPassword(
                                    loginInput.email,
                                    error = {
                                        loginViewModel.updateInput(
                                            loginInput.copy(
                                                email = "",
                                                password = "",
                                                error = it
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    )
                }
                BlackButton(
                    text = "SIGN IN",
                    action = {
                        loginViewModel.login(
                            role = user,
                            li = loginInput,
                            navigate = {
                                Toast.makeText(context, "User Logged In", Toast.LENGTH_LONG).show()
                                navController.navigate(Routes.DASHBOARD) {
                                    popUpTo(navController.graph.id) {
                                        inclusive = false
                                    }
                                }
                            },
                            error = {
                                loginViewModel.updateInput(
                                    loginInput.copy(
                                        email = "",
                                        password = "",
                                        error = it
                                    )
                                )
                            }
                        )
                    },
                    modifier = Modifier
                        .padding(horizontal = 30.dp)
                        .fillMaxWidth(),
                    enabled = enabled
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(30.dp)
                ) {
                    Text(
                        text = "No account yet?",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Signup Now",
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { navController.navigate("${Routes.SIGNUP}/$user") }
                    )
                }
                Spacer(modifier = Modifier.size(100.dp))
            }
        }
    }
}