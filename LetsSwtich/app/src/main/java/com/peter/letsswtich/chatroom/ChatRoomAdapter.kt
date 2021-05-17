package com.peter.letsswtich.chatroom

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.peter.letsswtich.data.Message
import com.peter.letsswtich.databinding.ItemFriendsMessageBinding
import com.peter.letsswtich.databinding.ItemMyMessageBinding
import com.peter.letsswtich.login.UserManager

class ChatRoomAdapter : ListAdapter<Message, RecyclerView.ViewHolder>(DiffCallback){

    class FriendMessageViewHolder(private var binding: ItemFriendsMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {

            binding.message = message
            binding.executePendingBindings()

        }
    }

    class MyMessageViewHolder(private var binding: ItemMyMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {

            binding.message = message
            binding.executePendingBindings()

        }
    }
    companion object DiffCallback : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.id == newItem.id
        }

        private const val ITEM_VIEW_TYPE_FRIEND = 0x00
        private const val ITEM_VIEW_TYPE_MY = 0x01
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_FRIEND -> FriendMessageViewHolder(ItemFriendsMessageBinding.inflate(
                LayoutInflater.from(parent.context), parent, false))
            ITEM_VIEW_TYPE_MY -> MyMessageViewHolder(ItemMyMessageBinding.inflate(
                LayoutInflater.from(parent.context), parent, false))
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder) {
            is FriendMessageViewHolder -> {
                holder.bind((getItem(position) as Message))
            }
            is MyMessageViewHolder -> {
                holder.bind((getItem(position) as Message))
            }
        }
    }

    override fun getItemViewType(position: Int): Int {

        return when (getItem(position).senderEmail) {
            UserManager.user.email -> ITEM_VIEW_TYPE_MY
            else -> ITEM_VIEW_TYPE_FRIEND
        }
    }


}