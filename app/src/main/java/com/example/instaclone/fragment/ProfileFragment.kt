package com.example.instaclone.fragment

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.instaclone.R
import com.example.instaclone.adapter.ProfileAdapter
import com.example.instaclone.adapter.SearchAdapter
import com.example.instaclone.manager.AuthManager
import com.example.instaclone.manager.DatabaseManager
import com.example.instaclone.manager.StorageManager
import com.example.instaclone.manager.handler.DBPostsHandler
import com.example.instaclone.manager.handler.DBUserHandler
import com.example.instaclone.manager.handler.StorageHandler
import com.example.instaclone.model.Post
import com.example.instaclone.model.User
import com.example.instaclone.utils.Logger
import com.google.android.material.imageview.ShapeableImageView
import com.sangcomz.fishbun.FishBun
import com.sangcomz.fishbun.adapter.image.impl.GlideAdapter
import java.lang.Exception

class ProfileFragment : BaseFragment() {
    val TAG = SearchFragment::class.java.simpleName
    lateinit var recyclerView: RecyclerView
    lateinit var iv_profile: ShapeableImageView
    lateinit var tv_fullname: TextView
    lateinit var tv_posts: TextView
    lateinit var tv_email: TextView
    lateinit var iv_plus: ImageView
    var pickedPhoto: Uri? = null
    var allPhotos =  ArrayList<Uri>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        initViews(view)
        return view
    }

    private fun initViews(view: View) {
        recyclerView = view.findViewById(R.id.recyclerView)
        tv_posts = view.findViewById(R.id.tv_posts)
        recyclerView.setLayoutManager(GridLayoutManager(activity, 2))
        val iv_logout = view.findViewById<ImageView>(R.id.iv_logout)
        iv_logout.setOnClickListener {
            AuthManager.signOut()
            callSignInActivity(requireActivity())
        }

        tv_fullname = view.findViewById(R.id.tv_fullname)
        tv_email = view.findViewById(R.id.tv_email)
        iv_plus = view.findViewById<ImageView>(R.id.iv_plus)

        iv_profile = view.findViewById(R.id.iv_profile)
        iv_profile.setOnClickListener {
            pickFishBunPhoto()
        }

        loadUserInfo()
        loadMyPosts()
    }

    private fun loadMyPosts() {
        val uid = AuthManager.currentUser()!!.uid
        DatabaseManager.loadPosts(uid, object : DBPostsHandler {
            override fun onSuccess(posts: ArrayList<Post>) {
                tv_posts.text = posts.size.toString()
                refreshAdapter(posts)
            }

            override fun onError(exception: Exception) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun loadUserInfo() {
        DatabaseManager.loadUser(AuthManager.currentUser()!!.uid, object : DBUserHandler {
            override fun onSuccess(user: User?) {
                if (user != null) {
                    iv_plus.visibility = View.GONE
                    showUserInfo(user)
                }
            }
            override fun onError(exception: Exception?) {
            }
        })
    }

    private fun pickFishBunPhoto() {
        FishBun.with(this)
            .setImageAdapter(GlideAdapter())
            .setMaxCount(1)
            .setMinCount(1)
            .setSelectedImages(allPhotos)
            .startAlbumWithActivityResultCallback(photoLauncher)
    }

    val photoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            allPhotos = it.data?.getParcelableArrayListExtra(FishBun.INTENT_PATH) ?: arrayListOf()
            pickedPhoto = allPhotos.get(0)
            uploadUserPhoto()
        }
    }

    private fun uploadUserPhoto() {
      if (pickedPhoto == null) return
        StorageManager.uploadUserPhoto(pickedPhoto!!, object : StorageHandler {
            override fun onSuccess(imgUrl: String) {
                DatabaseManager.updateUserImage(imgUrl)
                iv_profile.setImageURI(pickedPhoto)
            }
            override fun onError(exception: Exception?) {

            }
        })
    }

    private fun showUserInfo(user: User) {
        tv_fullname.text = user.fullname
        tv_email.text = user.email
        Glide.with(this).load(user.userImg)
            .placeholder(R.drawable.iv_profile)
            .error(R.drawable.iv_profile)
            .into(iv_profile)
    }

    private fun refreshAdapter(items: ArrayList<Post>) {
        val adapter = ProfileAdapter(this, items)
        recyclerView.adapter = adapter
    }


}