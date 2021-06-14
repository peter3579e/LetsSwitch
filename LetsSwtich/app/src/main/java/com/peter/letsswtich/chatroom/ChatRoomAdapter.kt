package com.peter.letsswtich.chatroom

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.*
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.peter.letsswtich.MainViewModel
import com.peter.letsswtich.NavigationDirections
import com.peter.letsswtich.data.Message
import com.peter.letsswtich.data.User
import com.peter.letsswtich.databinding.ItemFriendsMessageBinding
import com.peter.letsswtich.databinding.ItemMyMessageBinding
import com.peter.letsswtich.login.UserManager

class ChatRoomAdapter(val viewModel: ChatRoomViewModel) :
    ListAdapter<Message, RecyclerView.ViewHolder>(DiffCallback) {
//    var count = 0

    class FriendMessageViewHolder(private var binding: ItemFriendsMessageBinding) :
        RecyclerView.ViewHolder(binding.root), LifecycleOwner {



        fun bind(message: Message, viewModel: ChatRoomViewModel) {

            Log.d("ChatAdpater","FriendMessageViewHolder has run!!")

            binding.message = message
            binding.executePendingBindings()

            binding.root.setOnClickListener { view: View ->
                view.findNavController()
                    .navigate(NavigationDirections.navigateToProfileFragment(viewModel.userDetail.value!!,false))

            }
        }

        private val lifecycleRegistry = LifecycleRegistry(this)

        init {
            lifecycleRegistry.currentState = Lifecycle.State.INITIALIZED
        }

        override fun getLifecycle(): Lifecycle {
            return lifecycleRegistry
        }
    }

    class MyMessageViewHolder(private var binding: ItemMyMessageBinding) :
        RecyclerView.ViewHolder(binding.root), LifecycleOwner {
        val read = binding.read
        fun bind(message: Message, viewModel: ChatRoomViewModel) {
            viewModel.count++
            Log.d("ChatRoomAdpater","MyMessageViewHolder has run!!")

            binding.viewModel = viewModel

            binding.message = message

            val read = binding.read


            val size = viewModel.filterMessage.size
            Log.d("ChatRoomAdapter","value of size $size")
            Log.d("ChatRoomAdapter","value of count ${viewModel.count}")
//            Log.d("ChatRoomAdapter"," the last message ${viewModel.filterMessage[size].read}")

            Log.d("ChatRoomAdapter","the value of boolean = ${viewModel.filterMessage[size-1].read}")

                if (viewModel.filterMessage[size-1].read && viewModel.count == size) {
                    Log.d("ChatRoomAdapter","the visible has run")
                    read.visibility = View.VISIBLE
                    viewModel.count = 0
                } else {
                    Log.d("ChatRoomAdapter","the invisible has run")
                    read.visibility = View.GONE
                }


            binding.executePendingBindings()

        }

        private val lifecycleRegistry = LifecycleRegistry(this)

        init {
            lifecycleRegistry.currentState = Lifecycle.State.INITIALIZED
        }

        override fun getLifecycle(): Lifecycle {
            return lifecycleRegistry
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
            ITEM_VIEW_TYPE_FRIEND -> FriendMessageViewHolder(
                ItemFriendsMessageBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
            ITEM_VIEW_TYPE_MY -> MyMessageViewHolder(
                ItemMyMessageBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder) {
            is FriendMessageViewHolder -> {
                holder.bind((getItem(position) as Message), viewModel)
            }
            is MyMessageViewHolder -> {

                holder.bind((getItem(position) as Message), viewModel)
                Log.d("ChatRoomAdapter", "value of Item = ${getItem(position).text}")


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