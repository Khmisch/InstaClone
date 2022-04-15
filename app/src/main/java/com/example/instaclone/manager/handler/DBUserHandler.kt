package com.example.instaclone.manager.handler

import com.example.instaclone.model.User
import java.lang.Exception

interface DBUserHandler {
    fun onSuccess(user: User? = null)
    fun onError(exception: Exception?)
}