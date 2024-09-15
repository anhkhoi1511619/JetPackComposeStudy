package com.example.jetpackcomposeexample.model.experience

import com.example.jetpackcomposeexample.R

val experiencesExample1 = Experiences(
    id = "1",
    title = "Quan li He thong tau/bus tai Hiroshima",
    subtitle = "Vai tro: Developer",
    years = 2,
    likeCounter = 100,
    commentCounter = 1,
    imageId = R.drawable.post_3
)
val experiencesExample2 = Experiences(
    id = "2",
    title = "Quan li duong di cho Benh Vien",
    subtitle = "Vai tro: Developer",
    years = 2,
    likeCounter = 99,
    commentCounter = 1,
    imageId = R.drawable.post_3
)
val experiencesExample3 = Experiences(
    id = "3",
    title = "Nhan dien giong noi",
    subtitle = "Vai tro: Developer",
    years = 1,
    likeCounter = 98,
    commentCounter = 1,
    imageId = R.drawable.post_3
)
val experiencesExample4 = Experiences(
    id = "4",
    title = "Ngon ngu trao doi",
    subtitle = "Tieng Nhat",
    years = 3,
    likeCounter = 97,
    commentCounter = 1,
    imageId = R.drawable.post_3
)

val experiencesExampleList = listOf<Experiences>(experiencesExample1, experiencesExample2, experiencesExample3, experiencesExample4)