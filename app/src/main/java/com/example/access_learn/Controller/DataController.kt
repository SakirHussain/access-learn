package com.example.access_learn.Controller

import LanguageLearningApp3
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.access_learn.Views.Comprehension.LanguageLearningApp1
import com.example.access_learn.Views.Comprehension.LanguageLearningApp2
import kotlinx.serialization.Serializable

@Composable
fun myNavHost(){

    val navController = rememberNavController()
    
    NavHost(navController = navController, startDestination = "screen1"){
        composable("screen1"){
            LanguageLearningApp1(navController)
        }
        composable("screen2") {
            LanguageLearningApp2(navController)
        }
        composable("screen3") {
            LanguageLearningApp3()
        }
    }
}