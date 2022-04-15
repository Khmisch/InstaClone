package com.example.instaclone.manager.handler

import com.example.instaclone.model.Post
import java.lang.Exception

interface DBPostHandler {
    fun onSuccess(post: Post)
    fun onError(exception: Exception)
}