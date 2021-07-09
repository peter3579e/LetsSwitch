package com.peter.letsswtich.profile

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.peter.letsswtich.chat.NewMatchedAdapter
import com.peter.letsswtich.data.ChatRoom
import com.peter.letsswtich.databinding.ItemNewMatchesBinding
import com.peter.letsswtich.databinding.ItemProfilePhotosBinding
import kotlin.math.log

class PhotosAdapter(val viewModel: ProfileViewModel) :
    ListAdapter<String, PhotosAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(private var binding: ItemProfilePhotosBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(image: String) {
            image.let {
                binding.images = image
                binding.executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemProfilePhotosBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.itemView.setOnClickListener {
            Log.d("PhotosAdapter", "the value of Pic = ${getItem(position)}")
            viewModel.clickedPic.value = getItem(position)
        }
    }


    companion object DiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

}