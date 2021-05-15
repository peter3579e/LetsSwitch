package com.peter.letsswtich.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.peter.letsswtich.NavigationDirections
import com.peter.letsswtich.data.ChatRoom
import com.peter.letsswtich.databinding.FragmentProfileBinding
import com.peter.letsswtich.databinding.ItemChatListBinding

class ChatListAdapter(viewModel: ChatViewModel) : ListAdapter<ChatRoom, ChatListAdapter.ChatListViewHolder> (DiffCallback){
    class ChatListViewHolder(private var binding: ItemChatListBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(chatRoom: ChatRoom){

            binding.chatRoom = chatRoom

            // Chat room has been filtered, the attendee info only holds the other user's info
            val friendInfo = chatRoom.attendeesInfo.first()

            binding.textChatName.text = friendInfo.userName
            binding.image = friendInfo.userImage

            binding.layoutChatList.setOnClickListener {
                Navigation.createNavigateOnClickListener(NavigationDirections.navigateToChatroomFragment()).onClick(binding.layoutChatList)
            }

            binding.executePendingBindings()

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListViewHolder {
        return ChatListViewHolder(ItemChatListBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ChatListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }



    companion object DiffCallback : DiffUtil.ItemCallback<ChatRoom>() {
        override fun areItemsTheSame(oldItem: ChatRoom, newItem: ChatRoom): Boolean {
            return oldItem === newItem
        }
        override fun areContentsTheSame(oldItem: ChatRoom, newItem: ChatRoom): Boolean {
            return oldItem.chatRoomId == newItem.chatRoomId
        }
    }
}