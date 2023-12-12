package com.serrano.academically.activity

import com.serrano.academically.R
import com.serrano.academically.custom_composables.DiagonalBackground
import com.serrano.academically.ui.theme.Strings
import com.serrano.academically.custom_composables.BlackButton
import com.serrano.academically.custom_composables.LoginTextField
import com.serrano.academically.viewmodel.SignupViewModel
import android.content.Context
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.runBlocking

@Composable
fun Signup(
    navController: NavController,
    user: String,
    context: Context,
    courseId: Int,
    score: Int,
    items: Int,
    eval: Double,
    signupViewModel: SignupViewModel = hiltViewModel()
) {
    val signupInput by signupViewModel.signupInput.collectAsState()

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
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(350.dp)
                .offset(y = 244.dp)
        )
        Icon(
            imageVector = Icons.Default.ArrowBackIosNew,
            contentDescription = null,
            tint = Color.Black,
            modifier = Modifier.align(Alignment.TopStart).padding(30.dp).clickable { navController.navigateUp() }
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Create $user Account",
                color = Color.White,
                style = MaterialTheme.typography.labelMedium
            )
            LoginTextField(
                inputName = "Username",
                input = signupInput.name,
                onInputChange = { signupViewModel.updateInput(signupInput.copy(name = it)) },
                supportingText = "Should be 5-20 characters",
                modifier = Modifier.padding(horizontal = 30.dp)
            )
            LoginTextField(
                inputName = "Email",
                input = signupInput.email,
                onInputChange = { signupViewModel.updateInput(signupInput.copy(email = it)) },
                supportingText = "Should be 15-40 characters",
                modifier = Modifier.padding(horizontal = 30.dp)
            )
            LoginTextField(
                inputName = "Password",
                input = signupInput.password,
                onInputChange = { signupViewModel.updateInput(signupInput.copy(password = it)) },
                visualTransformation = PasswordVisualTransformation(),
                supportingText = "Should have at least one letter and number, and 8-20 characters",
                modifier = Modifier.padding(horizontal = 30.dp)
            )
            LoginTextField(
                inputName = "Confirm Password",
                input = signupInput.confirmPassword,
                onInputChange = { signupViewModel.updateInput(signupInput.copy(confirmPassword = it)) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.padding(horizontal = 30.dp)
            )
            Text(
                text = signupInput.error,
                color = Color.Red,
                style = MaterialTheme.typography.bodyMedium
            )
            BlackButton(
                text = Strings.createAccount,
                action = {
                    signupViewModel.validateUserSignUpAsynchronously(
                        context = context,
                        role = user,
                        si = signupInput,
                        courseId = courseId,
                        score = score,
                        items = items,
                        eval = eval,
                        navigate = { navController.navigate(it) },
                        error = { signupViewModel.updateInput(signupInput.copy(name = "", email = "", password = "", confirmPassword = "", error = it)) }
                    )
                },
                modifier = Modifier.padding(horizontal = 30.dp).fillMaxWidth()
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(30.dp)
            ) {
                Text(
                    text = Strings.haveAccount,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = Strings.logIn,
                    color = Color.Blue,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { navController.navigate("Login/$user") }
                )
            }
            Spacer(modifier = Modifier.size(100.dp))
        }
    }
}