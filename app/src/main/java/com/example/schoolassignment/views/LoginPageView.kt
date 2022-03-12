package com.example.schoolassignment.views

import androidx.activity.ComponentActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.schoolassignment.R
import com.example.schoolassignment.viewModels.LoginViewModel

enum class LoginState { EMAIL_OBTAINING, REGISTERING, SIGNING_IN }

@Composable
fun LoginPageView(navController: NavHostController) {

    val loginVM = viewModel<LoginViewModel>(LocalContext.current as ComponentActivity)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        //verticalArrangement = Arrangement.Center
    ) {

        Text(text = loginVM.getActionDescription())

        Spacer(modifier = Modifier.height(5.dp))

        EmailInputForm(loginVM)


        if (loginVM.loginPageState.value != LoginState.EMAIL_OBTAINING) {
            PasswordInputForm(
                placeholder = "Password",
                password = loginVM.password,
                isPasswordValid = loginVM.isPasswordValid
            )
        }

        if (loginVM.loginPageState.value == LoginState.REGISTERING) {
            PasswordInputForm(
                placeholder = "Confirm password",
                password = loginVM.confirmedPassword,
                isPasswordValid = loginVM.passwordsAreMatching
            )
        }

        if (!loginVM.isLoginPageWithoutError())
            ErrorNotification(errorMessage = loginVM.errorMsg.value)

        Spacer(modifier = Modifier.height(15.dp))

        Box(
            modifier = Modifier.fillMaxWidth(0.7f),
            contentAlignment = Alignment.CenterEnd
        ) {
            when (loginVM.loginPageState.value) {
                LoginState.EMAIL_OBTAINING -> ConfirmEmailButton(loginVM)
                LoginState.SIGNING_IN -> SignInButton(navController, loginVM)
                LoginState.REGISTERING -> CreateAccountButtons(navController, loginVM)
            }
        }
    }
}


@Composable
fun ConfirmEmailButton(loginVM: LoginViewModel) {

    OutlinedButton(onClick = { loginVM.confirmEmail() }) {
        Text(text = "NEXT")
    }
}

@Composable
fun SignInButton(navController: NavHostController, loginVM: LoginViewModel) {

    OutlinedButton(onClick = { loginVM.signInUser(navController) }) {
        Text(text = "SIGN IN")
    }
}

@Composable
fun CreateAccountButtons(navController: NavHostController, loginVM: LoginViewModel) {

    Row {
        TextButton(onClick = {
            loginVM.loginPageState.value = LoginState.EMAIL_OBTAINING
            loginVM.resetPasswords()
        }) {
            Text(text = "CANCEL")
        }

        Spacer(modifier = Modifier.width(10.dp))

        OutlinedButton(onClick = { loginVM.createUser(navController)}) {
            Text(text = "SAVE")
        }
    }
}


@Composable
fun ErrorNotification(errorMessage: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.7f)
            .padding(top = 5.dp),
        contentAlignment = Alignment.TopStart
    ) {
        Text(
            text = errorMessage,
            color = MaterialTheme.colors.error,
            style = MaterialTheme.typography.caption,
        )
    }
}

@Composable
fun EmailInputForm(loginVM: LoginViewModel) {

    TextField(
        value = loginVM.email.value,
        onValueChange = {
            loginVM.email.value = it
            if (!loginVM.isEmailValid.value) {
                loginVM.isEmailValid.value = true
            }
        },
        placeholder = { Text(text = "Enter your email") },
        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White),
        singleLine = true,
        isError = !loginVM.isEmailValid.value,
        readOnly = loginVM.loginPageState.value != LoginState.EMAIL_OBTAINING

    )

}

@Composable
fun PasswordInputForm(
    placeholder: String,
    password: MutableState<String>,
    isPasswordValid: MutableState<Boolean>
) {

    var showPassword by remember { mutableStateOf(false) }

    TextField(
        value = password.value,
        onValueChange = {
            //not allowing whitespace characters in password
            val passwordNoWhitespace = it.replace("\\s".toRegex(), "")
            if (password.value != passwordNoWhitespace) {
                password.value = passwordNoWhitespace
                if (!isPasswordValid.value) {
                    isPasswordValid.value = true
                }
            }
        },
        placeholder = { Text(text = placeholder) },
        singleLine = true,
        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White),
        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
        isError = !isPasswordValid.value,
        trailingIcon = {
            Icon(
                painter = painterResource(
                    if (showPassword) R.drawable.ic_baseline_visibility_off_24
                    else R.drawable.ic_baseline_visibility_24
                ),
                contentDescription = "Check password",
                modifier = Modifier.clickable { showPassword = !showPassword }
            )
        }
    )
}