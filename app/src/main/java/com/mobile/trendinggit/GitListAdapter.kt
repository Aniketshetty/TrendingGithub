package com.mobile.trendinggit

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mobile.trendinggit.Data.Repository
import java.io.ByteArrayOutputStream


class GitListAdapter(gitEvents: GitEvents) :
    RecyclerView.Adapter<GitListAdapter.GitHolder>() {

    private var gitRepoList: List<Repository> = arrayListOf()
    private var filteredGitRepoList: List<Repository> = arrayListOf()
    private lateinit var gitEvents: GitEvents

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GitHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.git_tm, parent, false)
        return GitHolder(view, gitEvents)
    }

    override fun getItemCount(): Int = filteredGitRepoList.size

    init {
        this.gitEvents = gitEvents
    }

    fun StringToBitMap(encodedString: String?): Bitmap? {
        return try {
            val encodeByte: ByteArray = Base64.decode(encodedString, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
        } catch (e: Exception) {
            e.message
            null
        }
    }

    private fun bitmapToByte(bitmap: Bitmap): ByteArray? {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        return stream.toByteArray()
    }

    override fun onBindViewHolder(holder: GitHolder, position: Int) {
        Glide.with(holder.iv_item_profile.context)
//            .load(StringToBitMap(filteredGitRepoList[position].avatar)?.let { bitmapToByte(it) }).asBitmap()
            .load(filteredGitRepoList[position].avatar)
            .error(R.drawable.baseline_account_circle_black_48)
            .into(holder.iv_item_profile)


        holder.tv_name.text = filteredGitRepoList[position].name
        holder.tv_desc.text = filteredGitRepoList[position].description

        holder.tv_desc.setOnClickListener {
            gitEvents.onGitClicked(filteredGitRepoList[position].id)
        }

    }

    inner class GitHolder(itemView: View, gitEvents: GitEvents) :
        RecyclerView.ViewHolder(itemView) {
        var tv_name: TextView = itemView.findViewById(R.id.tv_item_title)
        var tv_desc: TextView = itemView.findViewById(R.id.tv_item_content)
        var iv_item_profile: ImageView = itemView.findViewById(R.id.iv_item_profile)
        var gitEvents: GitEvents

        init {
            this.gitEvents = gitEvents
        }
    }


    /**f
     * Activity uses this method to update todoList with the help of LiveData
     * */
    fun setAllGitTMItems(gitItems: List<Repository>) {
        this.gitRepoList = gitItems
        this.filteredGitRepoList = gitItems
        notifyDataSetChanged()
    }

    /**
     * RecycleView touch event callbacks
     * */
    interface GitEvents {
        fun onGitClicked(repository: Int)
    }
}
