package com.peter.letsswtich.map.eventDetail

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.peter.letsswtich.chat.ChatViewModel
import com.peter.letsswtich.chat.NewMatchedAdapter
import com.peter.letsswtich.data.ChatRoom
import com.peter.letsswtich.databinding.ItemJoinImageBinding
import com.peter.letsswtich.databinding.ItemNewMatchesBinding

class JoinListAdapter (val viewModel: EventDetailViewModel): ListAdapter<String, JoinListAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(private var binding: ItemJoinImageBinding):
            RecyclerView.ViewHolder(binding.root) {
        fun bind(image: String) {
            image.let {
                Log.d("joinLsitAdapter","the value of received string = $it")
                binding.images = image
                binding.executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemJoinImageBinding.inflate(
                LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
        Log.d("joinLsitAdapter","$position")
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