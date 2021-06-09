package com.peter.letsswtich.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.peter.letsswtich.data.ChatRoom
import com.peter.letsswtich.data.UserInfo
import com.peter.letsswtich.databinding.ItemNewMatchesBinding

class NewMatchedAdapter(viewModel: ChatViewModel): ListAdapter<ChatRoom, NewMatchedAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(private var binding: ItemNewMatchesBinding):
            RecyclerView.ViewHolder(binding.root) {
                fun bind(image: ChatRoom) {
                    image.let {
                        val friendinfo = image.attendeesInfo.first()
                        binding.image = friendinfo.userImage
                        binding.executePendingBindings()
                    }
                }
            }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemNewMatchesBinding.inflate(
            LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    companion object DiffCallback : DiffUtil.ItemCallback<ChatRoom>() {
        override fun areItemsTheSame(oldItem: ChatRoom, newItem: ChatRoom): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: ChatRoom, newItem: ChatRoom): Boolean {
            return oldItem == newItem
        }
    }
}