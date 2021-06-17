package com.peter.letsswtich.home

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.peter.letsswtich.MainActivity
import com.peter.letsswtich.databinding.ItemImageCircleBinding

class ImageCircleAdapter : RecyclerView.Adapter<ImageCircleAdapter.ImageViewHolder>(){

    private lateinit var context: Context
    private var count = 0
    var selectedPosition = MutableLiveData<Int>()

    class ImageViewHolder(val binding: ItemImageCircleBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(context: Context, selectedPosition: MutableLiveData<Int>) {

            selectedPosition.observe(context as MainActivity, Observer {
                binding.isSelected = it == adapterPosition
                binding.executePendingBindings()
                Log.i("Selected position changed in adapter", "$it")
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageCircleAdapter.ImageViewHolder {
        context = parent.context
        return ImageViewHolder(
                ItemImageCircleBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false))
    }
    /**
     * Replaces the contents of a view (invoked by the layout manager)
     */
    override fun onBindViewHolder(holder: ImageCircleAdapter.ImageViewHolder, position: Int) {
        holder.bind(context, selectedPosition)
    }
    override fun getItemCount(): Int {
        return count
    }
    /**
     * Submit data list and refresh adapter by [notifyDataSetChanged]
     * @param count: set up count of circles
     */
    fun submitCount(count: Int) {
        this.count = count
        notifyDataSetChanged()
    }
}