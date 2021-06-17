package com.peter.letsswtich.map

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.peter.letsswtich.databinding.ItemEditPhotosBinding
import com.peter.letsswtich.editprofile.EditPhotoAdapter
import com.peter.letsswtich.editprofile.EditViewModel
import com.peter.letsswtich.map.event.EditEventViewModel

class EventPhotoAdapter (val viewModel: EditEventViewModel) : ListAdapter<String, EventPhotoAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(private var binding: ItemEditPhotosBinding):
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
            ItemEditPhotosBinding.inflate(
                LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.itemView.setOnClickListener {
            Log.d("EditPhotoAdapter","click value = $position")
            if(getItem(position) != "") {
                val list = viewModel.photoList.value
                Log.d("EditPhotoAdapter","value of click list before = ${viewModel.photoList.value}")
                list!!.remove(getItem((position)))
                list.add("")
                viewModel.photoList.value = list
                Log.d("EditPhotoAdapter","value of click list After = ${viewModel.photoList.value}")
            }else {
                viewModel.startCamera()
            }
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