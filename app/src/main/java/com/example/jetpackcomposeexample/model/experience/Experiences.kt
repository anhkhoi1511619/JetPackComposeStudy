package com.example.jetpackcomposeexample.model.experience

import androidx.annotation.DrawableRes

data class Experiences(
    val id: String,
    val title: String,
    val subtitle: String,
    val years: Int,
    val likeCounter: Int,
    val commentCounter:Int,
    @DrawableRes val imageId: Int
)