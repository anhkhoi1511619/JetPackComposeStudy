package com.example.jetpackcomposeexample.view.vico.viewModel

import com.example.jetpackcomposeexample.model.helper.dto.Post
import com.example.jetpackcomposeexample.model.helper.dto.impl.post1
import com.example.jetpackcomposeexample.model.helper.dto.impl.post2
import com.example.jetpackcomposeexample.model.helper.dto.impl.post3

data class PostUiState (
     val postList: List<Post> = listOf(post1, post2, post3),
     val isLoadedData: Boolean = false,
     val isLoadError: Boolean = false,
 )