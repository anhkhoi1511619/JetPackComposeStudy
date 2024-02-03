package com.example.jetpackcomposeexample.model.post

import com.example.jetpackcomposeexample.R

val publication = Publication(
    "Android Developers",
    "https://cdn-images-1.medium.com/max/258/1*u7oZc2_5mrkcFaxkXEyfYA@2x.png"
)
val florina = PostAuthor(
    "Florina Muntenescu",
    "https://medium.com/@florina.muntenescu"
)

val paragraphsPost3 = listOf(
    Paragraph(
        ParagraphType.Text,
        "Learn how to get started converting Java Programming Language code to Kotlin, making it more idiomatic and avoid common pitfalls, by following our new Refactoring to Kotlin codelab, available in English \uD83C\uDDEC\uD83C\uDDE7, Chinese \uD83C\uDDE8\uD83C\uDDF3 and Brazilian Portuguese \uD83C\uDDE7\uD83C\uDDF7.",
        listOf(
            Markup(
                MarkupType.Link,
                151,
                172,
                "https://codelabs.developers.google.com/codelabs/java-to-kotlin/#0"
            ),
            Markup(
                MarkupType.Link,
                209,
                216,
                "https://clmirror.storage.googleapis.com/codelabs/java-to-kotlin-zh/index.html#0"
            ),
            Markup(
                MarkupType.Link,
                226,
                246,
                "https://codelabs.developers.google.com/codelabs/java-to-kotlin-pt-br/#0"
            )
        )
    ),
    Paragraph(
        ParagraphType.Text,
        "When you first get started writing Kotlin code, you tend to follow Java Programming Language idioms. The automatic converter, part of both Android Studio and Intellij IDEA, can do a pretty good job of automatically refactoring your code, but sometimes, it needs a little help. This is where our new Refactoring to Kotlin codelab comes in.",
        listOf(
            Markup(
                MarkupType.Link,
                105,
                124,
                "https://www.jetbrains.com/help/idea/converting-a-java-file-to-kotlin-file.html"
            )
        )
    ),
    Paragraph(
        ParagraphType.Text,
        "We’ll take two classes (a User and a Repository) in Java Programming Language and convert them to Kotlin, check out what the automatic converter did and why. Then we go to the next level — make it idiomatic, teaching best practices and useful tips along the way.",
        listOf(
            Markup(MarkupType.Code, 26, 30),
            Markup(MarkupType.Code, 37, 47)
        )
    ),
    Paragraph(
        ParagraphType.Text,
        "The Refactoring to Kotlin codelab starts with basic topics — understand how nullability is declared in Kotlin, what types of equality are defined or how to best handle classes whose role is just to hold data. We then continue with how to handle static fields and functions in Kotlin and how to apply the Singleton pattern, with the help of one handy keyword: object. We’ll see how Kotlin helps us model our classes better, how it differentiates between a property of a class and an action the class can do. Finally, we’ll learn how to execute code only in the context of a specific object with the scope functions.",
        listOf(
            Markup(MarkupType.Code, 245, 251),
            Markup(MarkupType.Code, 359, 365),
            Markup(
                MarkupType.Link,
                4,
                25,
                "https://codelabs.developers.google.com/codelabs/java-to-kotlin/#0"
            )
        )
    ),
    Paragraph(
        ParagraphType.Text,
        "Thanks to Walmyr Carvalho and Nelson Glauber for translating the codelab in Brazilian Portuguese!",
        listOf(
            Markup(
                MarkupType.Link,
                21,
                42,
                "https://codelabs.developers.google.com/codelabs/java-to-kotlin/#0"
            )
        )
    ),
    Paragraph(
        ParagraphType.Text,
        "",
        listOf(
            Markup(
                MarkupType.Link,
                76,
                96,
                "https://codelabs.developers.google.com/codelabs/java-to-kotlin-pt-br/#0"
            )
        )
    )
)
val post3 = Post(
    id = "ac552dcc1741",
    title = "From Java Programming Language to Kotlin — the idiomatic way",
    subtitle = "Learn how to get started converting Java Programming Language code to Kotlin, making it more idiomatic and avoid common pitfalls, by…",
    url = "https://medium.com/androiddevelopers/from-java-programming-language-to-kotlin-the-idiomatic-way-ac552dcc1741",
    publication = publication,
    metaData = MetaData(
        author = florina,
        date = "July 09",
        readTimeMinutes = 1
    ),
    paragraph = paragraphsPost3,
    imageId = R.drawable.post_3,
    imageThumbId = R.drawable.post_3_thumb
)
val post1 = Post(
    id = "dc523f0ed25c",
    title = "A Little Thing about Android Module Paths",
    subtitle = "How to configure your module paths, instead of using Gradle’s default.",
    url = "https://medium.com/androiddevelopers/gradle-path-configuration-dc523f0ed25c",
    publication = publication,
    metaData = MetaData(
        author = florina,
        date = "August 02",
        readTimeMinutes = 1
    ),
    paragraph = paragraphsPost3,
    imageId = R.drawable.post_3,
    imageThumbId = R.drawable.post_3_thumb
)

val post2 = Post(
    id = "7446d8dfd7dc",
    title = "Dagger in Kotlin: Gotchas and Optimizations",
    subtitle = "Use Dagger in Kotlin! This article includes best practices to optimize your build time and gotchas you might encounter.",
    url = "https://medium.com/androiddevelopers/dagger-in-kotlin-gotchas-and-optimizations-7446d8dfd7dc",
    publication = publication,
    metaData = MetaData(
        author = florina,
        date = "July 30",
        readTimeMinutes = 3
    ),
    paragraph = paragraphsPost3,
    imageId = R.drawable.post_3,
    imageThumbId = R.drawable.post_3_thumb
)

val posts = listOf<Post>(post1, post2, post3)