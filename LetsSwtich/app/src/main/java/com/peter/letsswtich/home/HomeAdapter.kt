package com.peter.letsswtich.home

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.peter.letsswtich.data.User
import com.peter.letsswtich.databinding.ItemProfilecardBinding

class HomeAdapter(val viewModel: HomeViewModel):ListAdapter<User, RecyclerView.ViewHolder> (DiffCallback){


    class UserViewHolder(private var binding: ItemProfilecardBinding, viewModel: HomeViewModel): RecyclerView.ViewHolder(binding.root),LifecycleOwner {
        fun bind(user: User,viewModel: HomeViewModel){
            binding.lifecycleOwner = this
            binding.item = user

            val chipGroup = binding.chipGroup

            var language = user.fluentLanguage








            if (viewModel.count){
                language = emptyList()
            }

            Log.d("HomeAdapter","Adapter has run")

            for (language in language){
                val chip = Chip(chipGroup.context)
                chip.text = language
                chipGroup.addView(chip)
            }
            // This is important, because it forces the data binding to execute immediately,
            // which allows the RecyclerView to make the correct view size measurements
            binding.executePendingBindings()
        }

        private val lifecycleRegistry = LifecycleRegistry(this)

        init {
            lifecycleRegistry.currentState = Lifecycle.State.INITIALIZED
        }

        fun onAttach() {
            lifecycleRegistry.currentState = Lifecycle.State.STARTED
        }

        fun onDetach() {
            lifecycleRegistry.currentState = Lifecycle.State.CREATED
        }

        override fun getLifecycle(): Lifecycle {
            return lifecycleRegistry
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_USER -> UserViewHolder(ItemProfilecardBinding.inflate(
                LayoutInflater.from(parent.context), parent, false),viewModel)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is UserViewHolder -> {
                holder.bind(getItem(position) as User,viewModel)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return ITEM_VIEW_TYPE_USER
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        when (holder) {
            is UserViewHolder -> holder.onAttach()
        }
    }

    /**
     * It for [LifecycleRegistry] change [onViewDetachedFromWindow]
     */
    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        when (holder) {
            is UserViewHolder -> holder.onDetach()
        }
    }



    companion object DiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem === newItem
        }
        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        private const val ITEM_VIEW_TYPE_USER = 0x00
    }



}

