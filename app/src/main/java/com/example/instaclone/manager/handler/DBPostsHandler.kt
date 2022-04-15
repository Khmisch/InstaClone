package com.example.instaclone.manager.handler

import com.example.instaclone.model.Post
import java.lang.Exception

interface DBPostsHandler {
    fun onSuccess(posts: ArrayList<Post>)
    fun onError(exception: Exception)
}