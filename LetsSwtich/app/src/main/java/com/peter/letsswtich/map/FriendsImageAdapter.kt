package com.peter.letsswtich.map

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.peter.letsswtich.data.User
import com.peter.letsswtich.databinding.ItemMapFriendImagesBinding


class FriendsImageAdapter (val viewModel: MapViewModel): ListAdapter<User, FriendsImageAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(private var binding: ItemMapFriendImagesBinding):
            RecyclerView.ViewHolder(binding.root) {
        fun bind(image: User) {
            image.let {
                binding.user = image
                binding.executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemMapFriendImagesBinding.inflate(
                LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.itemView.setOnClickListener {
            Log.d("Mic"," the value of Use = ${getItem(position).latitude}")
            viewModel.friendslocation.value = LatLng(getItem(position).latitude,getItem(position).lngti)
            viewModel.clickedUserDetail.value = getItem(position)
            viewModel.showsMore.value = true
            viewModel.cardViewHeight.value = 550
        }
    }


    companion object DiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }

}