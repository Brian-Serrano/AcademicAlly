package com.serrano.academically.activity

import com.serrano.academically.R
import com.serrano.academically.custom_composables.DiagonalBackground
import com.serrano.academically.ui.theme.Strings
import com.serrano.academically.custom_composables.BlackButton
import com.serrano.academically.custom_composables.LoginTextField
import com.serrano.academically.datastore.UpdateUserPref
import com.serrano.academically.datastore.UserPref
import com.serrano.academically.datastore.dataStore
import com.serrano.academically.viewmodel.LoginViewModel
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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
fun Login(
    navController: NavController,
    user: String,
    context: Context,
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    val loginInput by loginViewModel.loginInput.collectAsState()
    val userPref by context.dataStore.data.collectAsState(initial = UserPref())

    LaunchedEffect(Unit) {
        if (userPref.isRemember) {
            loginViewModel.updateInput(
                loginInput.copy(
                    email = userPref.email,
                    password = userPref.password,
                    remember = true
                )
            )
        }
    }

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
            verticalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Sign in as $user",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )
            LoginTextField(
                inputName = "Email",
                input = loginInput.email,
                onInputChange = { loginViewModel.updateInput(loginInput.copy(email = it)) },
                modifier = Modifier.padding(horizontal = 30.dp)
            )
            LoginTextField(
                inputName = "Password",
                input = loginInput.password,
                onInputChange = { loginViewModel.updateInput(loginInput.copy(password = it)) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.padding(horizontal = 30.dp)
            )
            Text(
                text = loginInput.error,
                color = Color.Red,
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
                        text = Strings.remember,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = Strings.forgotPass,
                    color = Color.Black,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            BlackButton(
                text = Strings.signIn,
                action = {
                    loginViewModel.validateUserLoginAsynchronously(
                        context,
                        user,
                        loginInput,
                        { navController.navigate(it) },
                        { loginViewModel.updateInput(loginInput.copy(email = "", password = "", error = it)) })
                },
                modifier = Modifier
                    .padding(horizontal = 30.dp)
                    .fillMaxWidth()
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(30.dp)
            ) {
                Text(
                    text = Strings.noAccount,
                    color = Color.Black,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = Strings.signUp,
                    color = Color.Blue,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { navController.navigate("Signup/$user/0/0/5/0") }
                )
            }
            Spacer(modifier = Modifier.size(100.dp))
        }
    }
}