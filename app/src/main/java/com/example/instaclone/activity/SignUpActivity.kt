package com.example.instaclone.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.instaclone.R
import com.example.instaclone.manager.AuthManager
import com.example.instaclone.manager.DatabaseManager
import com.example.instaclone.manager.handler.AuthHandler
import com.example.instaclone.manager.handler.DBUserHandler
import com.example.instaclone.model.User
import com.example.instaclone.utils.Extensions.toast
import com.example.instaclone.utils.Logger.d
import java.lang.Exception
import java.util.logging.Logger

/**
 * In SignUpActivity, user can sign up using fullname, email, password
 */
class SignUpActivity : BaseActivity() {
    val TAG = SignUpActivity::class.java.toString()
    lateinit var et_email: EditText
    lateinit var et_password: EditText
    lateinit var et_cpassword: EditText
    lateinit var et_fullname: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        initViews()
    }

    private fun initViews() {
        et_email = findViewById(R.id.et_email)
        et_password = findViewById(R.id.et_password)
        et_cpassword = findViewById(R.id.et_cpassword)
        et_fullname = findViewById(R.id.et_fullname)

        val b_signup = findViewById<Button>(R.id.b_signup)
        b_signup.setOnClickListener {
            val email = et_email.text.toString().trim()
            val fullname = et_fullname.text.toString().trim()
            val password = et_password.text.toString().trim()
            if (fullname.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                val user = User(fullname, email, password,"")
                firebaseSignUp(user)
            }
        }

        val tv_signin = findViewById<TextView>(R.id.tv_signin)
        tv_signin.setOnClickListener { finish() }
    }

    private fun firebaseSignUp(user: User) {
        showLoading(this)
        AuthManager.signUp(user.email, user.password, object : AuthHandler {
            override fun onSuccess(uid: String) {
                user.uid = uid
                storeUserToDB(user)
                toast(getString(R.string.str_signup_success))
            }

            override fun onError(exception: Exception?) {
                dismissLoading()
                toast(getString(R.string.str_signup_failed))
            }

        })
    }

    private fun storeUserToDB(user: User) {
        DatabaseManager.storeUser(user, object : DBUserHandler {
            override fun onSuccess(user: User?) {
                dismissLoading()
                callMainActivity(context)
            }

            override fun onError(exception: Exception?) {
                Log.d(TAG, exception.toString())
            }

        })
    }
}


