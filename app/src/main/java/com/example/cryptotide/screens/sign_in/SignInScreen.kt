package com.example.cryptotide.screens.sign_in

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController


@Composable
fun SignInScreen(
    modifier: Modifier = Modifier,
    signInViewModel: SignInViewModel = hiltViewModel(),
    navController: NavController
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Login",
            modifier = modifier
                .padding(16.dp),
            style = MaterialTheme.typography.headlineLarge
        )

        OutlinedTextField(
            singleLine = true,
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp, 4.dp),
            value = signInViewModel.email,
            onValueChange = { signInViewModel.updateEmail(it) },
            leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = "Email") },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )
        Spacer(
            modifier = modifier
                .fillMaxWidth()
                .padding(2.dp)
        )
        OutlinedTextField(
            singleLine = true,
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp, 4.dp),
            value = signInViewModel.password,
            onValueChange = { signInViewModel.updatePassword(it) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Password"
                )
            },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            )
        )
        Spacer(
            modifier = modifier
                .fillMaxWidth()
                .padding(12.dp)
        )
        Button(
            onClick = {
                signInViewModel.onSignInClick(onSuccessNavigation = {
                    navController.navigate("HOME_SCREEN") {
                        popUpTo("SING_IN_SCREEN") { inclusive = true }
                    }
                })
            },
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp, 0.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (signInViewModel.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(ButtonDefaults.IconSize),
                        strokeWidth = 2.dp
                    )
                }
                Text(
                    text = "Sign In",
                    fontSize = 16.sp,
                    modifier = modifier.padding(0.dp, 6.dp)
                )
            }

        }

        if (!signInViewModel.errorMessage.isNullOrEmpty()) {
            Text(
                text = signInViewModel.errorMessage!!,
                color = Color.Red,
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(
            modifier = modifier
                .fillMaxWidth()
                .padding(12.dp)
        )
        TextButton(onClick = {
            navController.navigate("SIGN_UP_SCREEN") {
                launchSingleTop = true
            }
        }) {
            Text(text = "Don't have an account? Click here to Sign up")
        }
    }
}


//@Preview(showBackground = true)
//@Composable
//fun SignInScreenPreview() {
//    val dummyViewModel = SignInViewModel()
//    SignInScreen(signInViewModel = dummyViewModel)
//}