package com.peter.letsswtich.editprofile.preview

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.peter.letsswtich.R
import com.peter.letsswtich.databinding.ItemProfileImageBinding
import com.peter.letsswtich.home.HomeViewModel
import com.peter.letsswtich.home.ImageAdapter

class PreviewImageAdapter (val viewModel: PreviewViewModel): RecyclerView.Adapter<PreviewImageAdapter.ImageViewHolder>() {
    private lateinit var context: Context
    private lateinit var images: List<String>
    val position: Int =-1

    class ImageViewHolder(private var binding: ItemProfileImageBinding): RecyclerView.ViewHolder(binding.root) {



        val view = binding.imageDetailGallery


        fun bind(context: Context, imageUrl: String) {

            Log.d("Alex","Run11")

            imageUrl.let {
                binding.imageUrl = it
                Log.d("Alex","Run12")


                // Make sure the size of each image item can display correct
                val displayMetrics = DisplayMetrics()
                Log.d("Alex","Run13")
                (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
                Log.d("Alex","Run14")
                binding.imageDetailGallery.layoutParams = ConstraintLayout.LayoutParams(displayMetrics.widthPixels,
                        context.resources.getDimensionPixelSize(R.dimen.height_profile_gallery))
                Log.d("Alex","Run15")
                binding.executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        context = parent.context
        return ImageViewHolder(ItemProfileImageBinding.inflate(
                LayoutInflater.from(parent.context), parent, false))
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        images.let {
            holder.bind(context,it[getRealPosition(position)])
        }

        viewModel._snapPosition.value = position

    }

    override fun getItemCount(): Int {
        return images.let { Int.MAX_VALUE } ?: 0
    }


    private fun getRealPosition(position: Int): Int = images.let {
        position % it.size
    } ?: 0

    fun submitImages(images: List<String>) {
        this.images = images
        notifyDataSetChanged()
    }
}