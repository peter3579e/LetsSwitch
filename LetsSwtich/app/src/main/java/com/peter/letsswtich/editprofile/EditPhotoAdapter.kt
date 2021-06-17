package com.peter.letsswtich.editprofile

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.peter.letsswtich.LetsSwtichApplication
import com.peter.letsswtich.MainActivity
import com.peter.letsswtich.R
import com.peter.letsswtich.databinding.ItemEditPhotosBinding
import com.peter.letsswtich.databinding.ItemProfilePhotosBinding
import com.peter.letsswtich.profile.PhotosAdapter
import java.io.File

class EditPhotoAdapter(val viewModel: EditViewModel) : ListAdapter<String, EditPhotoAdapter.ViewHolder>(DiffCallback) {
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
        return ViewHolder(ItemEditPhotosBinding.inflate(
            LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))

        Log.d("EditPhotoAdapter","value of click list before = ${viewModel.photoList.value}")
        holder.itemView.setOnClickListener {
            Log.d("EditPhotoAdapter","click value = $position")
            if(getItem(position) != "") {
                Log.d("EditPhotoAdapter","value of position = ${getItem(position)}")
                viewModel.photoList.value!!.remove(getItem((position)))
                Log.d("EditPhotoAdapter","value of list = ${viewModel.photoList.value}")
            }else {
                viewModel.startCamera()
            }

            viewModel.newPhotoList.value = viewModel.photoList.value
            Log.d("EditPhotoAdapter","value of click list after = ${viewModel.photoList.value}")
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