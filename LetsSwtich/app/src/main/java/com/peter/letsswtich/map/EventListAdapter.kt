package com.peter.letsswtich.map

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.peter.letsswtich.NavigationDirections
import com.peter.letsswtich.chat.ChatListAdapter
import com.peter.letsswtich.chat.ChatViewModel
import com.peter.letsswtich.chatroom.ChatRoomAdapter
import com.peter.letsswtich.data.ChatRoom
import com.peter.letsswtich.data.Events
import com.peter.letsswtich.databinding.ItemChatListBinding
import com.peter.letsswtich.databinding.ItemCreateEventBinding
import com.peter.letsswtich.databinding.ItemEventListBinding
import com.peter.letsswtich.login.UserManager

class EventListAdapter(val viewModel: MapViewModel) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var events: List<Events>? = null

    override fun getItemCount(): Int {

        Log.d("EventListAdapter", "return ${events?.size}")

        events?.let {
            return when (it.size) {
                null -> 0
                else -> it.size + 1
            }
        }
        return 0
    }

    class EventListViewHolder(private var binding: ItemEventListBinding) :
        RecyclerView.ViewHolder(binding.root), LifecycleOwner {

        val seeMore = binding.navigateToEventDetail

        fun bind(events: Events?, viewModel: MapViewModel) {

            events?.let {
                binding.eventDetail = it
                binding.viewModel = viewModel

                binding.executePendingBindings()
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

    class CreateEventViewHolder(private var binding: ItemCreateEventBinding) :
        RecyclerView.ViewHolder(binding.root), LifecycleOwner {

        fun bind(viewModel: MapViewModel) {

            binding.viewModel = viewModel

            binding.executePendingBindings()

        }

        fun onAttach() {
            lifecycleRegistry.currentState = Lifecycle.State.STARTED
        }

        fun onDetach() {
            lifecycleRegistry.currentState = Lifecycle.State.CREATED
        }

        private val lifecycleRegistry = LifecycleRegistry(this)

        init {
            lifecycleRegistry.currentState = Lifecycle.State.INITIALIZED
        }
        override fun getLifecycle(): Lifecycle {
            return lifecycleRegistry
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_Create -> CreateEventViewHolder(
                ItemCreateEventBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            ITEM_VIEW_TYPE_List -> EventListViewHolder(
                ItemEventListBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder) {
            is EventListViewHolder -> {
                holder.bind(events?.get(position - 1), viewModel)
                holder.seeMore.setOnClickListener {
                    viewModel.navigateToEventDetail.value = events?.get(position - 1)
                    Log.d("EventListAdapter", "navigate click value = ${events?.get(position - 1)}")
                    notifyDataSetChanged()
                }
                holder.itemView.setOnClickListener {
                    Log.d(
                        "EventListAdapter",
                        "the value of position = ${events?.get(position - 1)}"
                    )
                    viewModel.clickedEventLocation.value = events?.get(position - 1)
                }
            }
            is CreateEventViewHolder -> {
                holder.bind(viewModel)
                holder.itemView.setOnClickListener {
                    Log.d("EventListAdapter", "the value of position = $position")
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        Log.d("EventListAdapter", "the value of position = $position")
        if (position == 0) {
            return ITEM_VIEW_TYPE_Create
        } else {
            return ITEM_VIEW_TYPE_List
        }
    }

    fun submitEvent(events: List<Events>) {
        this.events = events
        Log.d("EventListAdapter", "event value in adapter = ${events.size}")
        notifyDataSetChanged()
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        when (holder) {
            is CreateEventViewHolder -> holder.onAttach()
        }
    }

    /**
     * It for [LifecycleRegistry] change [onViewDetachedFromWindow]
     */
    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        when (holder) {
            is CreateEventViewHolder -> holder.onDetach()
        }
    }


    companion object {
        private const val ITEM_VIEW_TYPE_Create = 0x00
        private const val ITEM_VIEW_TYPE_List = 0x01
    }


}