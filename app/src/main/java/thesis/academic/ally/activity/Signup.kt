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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import thesis.academic.ally.custom_composables.CustomInputField
import thesis.academic.ally.custom_composables.DiagonalBackground
import thesis.academic.ally.utils.Routes
import thesis.academic.ally.utils.Utils
import thesis.academic.ally.viewmodel.SignupViewModel

@Composable
fun Signup(
    navController: NavController,
    user: String,
    context: Context,
    signupViewModel: SignupViewModel = hiltViewModel()
) {
    val signupInput by signupViewModel.signupInput.collectAsState()
    val enabled by signupViewModel.buttonEnabled.collectAsState()

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
                verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = "Create $user Account",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.labelMedium
                )
                CustomInputField(
                    inputName = "Username",
                    input = signupInput.name,
                    onInputChange = { signupViewModel.updateInput(signupInput.copy(name = it)) },
                    supportingText = "Should be 5-20 characters",
                    modifier = Modifier.padding(horizontal = 30.dp),
                    isError = signupInput.name.length !in 5..20
                )
                CustomInputField(
                    inputName = "Email",
                    input = signupInput.email,
                    onInputChange = { signupViewModel.updateInput(signupInput.copy(email = it)) },
                    supportingText = "Should be 15-40 characters",
                    modifier = Modifier.padding(horizontal = 30.dp),
                    isError = !Utils.emailPattern.containsMatchIn(signupInput.email)
                )
                CustomInputField(
                    inputName = "Password",
                    input = signupInput.password,
                    onInputChange = { signupViewModel.updateInput(signupInput.copy(password = it)) },
                    visualTransformation = if (signupInput.passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                    supportingText = "Should have at least one letter and number, and 8-20 characters",
                    modifier = Modifier.padding(horizontal = 30.dp),
                    trailingIcon = {
                        IconButton(onClick = { signupViewModel.updateInput(signupInput.copy(passwordVisibility = !signupInput.passwordVisibility)) }) {
                            Icon(
                                if (signupInput.passwordVisibility) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null,
                                tint = Color.Black
                            )
                        }
                    },
                    isError = !Utils.passwordPattern.containsMatchIn(signupInput.password)
                )
                CustomInputField(
                    inputName = "Confirm Password",
                    input = signupInput.confirmPassword,
                    onInputChange = { signupViewModel.updateInput(signupInput.copy(confirmPassword = it)) },
                    visualTransformation = if (signupInput.confirmPasswordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.padding(horizontal = 30.dp),
                    trailingIcon = {
                        IconButton(onClick = { signupViewModel.updateInput(signupInput.copy(confirmPasswordVisibility = !signupInput.confirmPasswordVisibility)) }) {
                            Icon(
                                if (signupInput.confirmPasswordVisibility) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null,
                                tint = Color.Black
                            )
                        }
                    },
                    isError = !Utils.passwordPattern.containsMatchIn(signupInput.confirmPassword)
                )
                Text(
                    text = signupInput.error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
                BlackButton(
                    text = "CREATE ACCOUNT",
                    action = {
                        signupViewModel.signup(
                            role = user,
                            si = signupInput,
                            navigate = {
                                Toast.makeText(context, "User Signed Up!", Toast.LENGTH_LONG).show()
                                navController.navigate(Routes.DASHBOARD) {
                                    popUpTo(navController.graph.id) {
                                        inclusive = false
                                    }
                                }
                            },
                            error = {
                                signupViewModel.updateInput(signupInput.copy(error = it))
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
                        text = "Already have account?",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Login Now",
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { navController.navigate("${Routes.LOGIN}/$user") }
                    )
                }
                Spacer(modifier = Modifier.size(100.dp))
            }
        }
    }
}