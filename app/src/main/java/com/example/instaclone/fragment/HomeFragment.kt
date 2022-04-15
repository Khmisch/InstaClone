package com.example.instaclone.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instaclone.R
import com.example.instaclone.adapter.HomeAdapter
import com.example.instaclone.manager.AuthManager
import com.example.instaclone.manager.DatabaseManager
import com.example.instaclone.manager.handler.DBPostsHandler
import com.example.instaclone.model.Post
import java.lang.Exception
import java.lang.RuntimeException

class HomeFragment : BaseFragment() {
    val TAG = HomeFragment::class.java.simpleName
    lateinit var adapter: HomeAdapter
    lateinit var recyclerView: RecyclerView
    private var listener: HomeListener? = null
    lateinit var iv_camera: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        initViews(view)
        return view
    }

    /**
     * onAttach is for communication of Fragments
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = if (context is HomeFragment.HomeListener) {
            context
        } else {
            throw RuntimeException("$context must implement HomeListener")
        }
    }

    /**
     * onDetach is for communication of Fragments
     */
    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private fun initViews(view: View) {
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.setLayoutManager(GridLayoutManager(activity, 1))
        iv_camera = view.findViewById(R.id.iv_camera)
        iv_camera.setOnClickListener {
            listener!!.scrollToUpload()
        }

        loadMyPosts()
    }

    private fun loadMyPosts() {
        showLoading(requireActivity())
        val uid = AuthManager.currentUser()!!.uid
        DatabaseManager.loadFeeds(uid, object : DBPostsHandler {
            override fun onSuccess(posts: ArrayList<Post>) {
                dismissLoading()
                refreshAdapter(posts)
            }

            override fun onError(exception: Exception) {
                dismissLoading()
            }
        })
    }

    private fun refreshAdapter(items: ArrayList<Post>) {
        val adapter = HomeAdapter(this, items)
        recyclerView.adapter = adapter
    }

    /**
     * This interface is created for communication with UploadFragment
     */
    interface HomeListener {
        fun scrollToUpload()
    }
}