package com.example.cryptotide.screens.sign_in

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cryptotide.ui.theme.Purple40


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
                .padding(16.dp, 4.dp)
                .border(
                    BorderStroke(width = 2.dp, color = Purple40),
                    shape = RoundedCornerShape(50)
                ),
            value = signInViewModel.email,
            onValueChange = { signInViewModel.updateEmail(it) },
            leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = "Email") },
            label = { Text("Email") }
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
                .padding(16.dp, 4.dp)
                .border(
                    BorderStroke(width = 2.dp, color = Purple40),
                    shape = RoundedCornerShape(50)
                ),
            value = signInViewModel.password,
            onValueChange = { signInViewModel.updatePassword(it) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Password"
                )
            },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(
            modifier = modifier
                .fillMaxWidth()
                .padding(12.dp)
        )
        Button(
            onClick = { signInViewModel.onSignInClick({navController.navigate("HOME_SCREEN") {
                popUpTo("SING_IN_SCREEN") {inclusive = true}
            }})},
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp, 0.dp)
        ) {
            Text(
                text = "Sign In",
                fontSize = 16.sp,
                modifier = modifier.padding(0.dp, 6.dp)
            )
        }
        Spacer(
            modifier = modifier
                .fillMaxWidth()
                .padding(12.dp)
        )
        TextButton(onClick = {navController.navigate("SIGN_UP_SCREEN") { launchSingleTop = true }}) {
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