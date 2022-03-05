package com.example.schoolassignment

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginViewModel : ViewModel() {

    private val firebaseAuth = Firebase.auth

    val loginPageState = mutableStateOf(LoginState.EMAIL_OBTAINING)
    val email = mutableStateOf("")
    val password = mutableStateOf("")
    val confirmedPassword = mutableStateOf("")
    val isPasswordValid = mutableStateOf(true)
    val isEmailValid = mutableStateOf(true)
    val passwordsAreMatching = mutableStateOf(true)
    val errorMsg = mutableStateOf("")

    fun isLoginPageWithoutError() : Boolean {
        return (isEmailValid.value && passwordsAreMatching.value  && isPasswordValid.value)
    }

    fun resetPasswords() {
        password.value = ""
        confirmedPassword.value = ""
        isPasswordValid.value = true
        passwordsAreMatching.value = true
    }

    fun getActionDescription() : String {
        return when(loginPageState.value){
            LoginState.EMAIL_OBTAINING -> "Sign in with email"
            LoginState.SIGNING_IN -> "Sign in"
            LoginState.REGISTERING -> "Create account"
        }
    }

    fun confirmEmail() {
        if (email.value.trim().isEmpty()) {
            errorMsg.value = "Email field can not be empty!"
            isEmailValid.value = false
        } else {
            firebaseAuth.fetchSignInMethodsForEmail(email.value)
                .addOnSuccessListener {
                    if (it.signInMethods == null || it.signInMethods!!.isEmpty()) {
                        loginPageState.value = LoginState.REGISTERING
                    } else {
                        loginPageState.value = LoginState.SIGNING_IN
                    }
                }
                .addOnFailureListener {
                    errorMsg.value = "Email is not valid!"
                    isEmailValid.value = false
                }
        }
    }

    fun signInUser(navController: NavHostController) {

        if (password.value.isEmpty()) {
            errorMsg.value = "Password input field can not be empty!"
            isPasswordValid.value = false
        } else {
            firebaseAuth.signInWithEmailAndPassword(email.value, password.value)
                .addOnSuccessListener {
                    loginPageState.value = LoginState.EMAIL_OBTAINING
                    resetPasswords()
                    navController.navigate(SCAFFOLD_GAME_PAGE_ROUTE)
                }
                .addOnFailureListener {
                    errorMsg.value = "Incorrect password!"
                    isPasswordValid.value = false
                }
        }
    }

    fun createUser(navController: NavHostController){
        isPasswordValid.value = password.value.isNotEmpty()
        passwordsAreMatching.value = confirmedPassword.value.isNotEmpty()

        if (!isPasswordValid.value || !passwordsAreMatching.value) {
            errorMsg.value = "Neither of the passwords can be empty!"
        } else if (password.value == confirmedPassword.value) {
            firebaseAuth.createUserWithEmailAndPassword(
                email.value, password.value
            )
                .addOnSuccessListener {
                    loginPageState.value = LoginState.EMAIL_OBTAINING
                    resetPasswords()
                    navController.navigate(SCAFFOLD_GAME_PAGE_ROUTE)
                }
                .addOnFailureListener {
                    errorMsg.value = it.message.toString()
                    isPasswordValid.value = false
                }
        } else {
            errorMsg.value = "Passwords are not matching!"
            passwordsAreMatching.value = false
        }
    }

    fun signOut(){
        firebaseAuth.signOut()
    }

    fun getUserEmail(): String {
        return firebaseAuth.currentUser!!.email!!
    }

}