package com.peter.letsswtich

import android.util.Log
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.peter.letsswtich.data.User
import com.peter.letsswtich.home.HomeAdapter

@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = it.toUri().buildUpon().build()
        Glide.with(imgView.context)
            .load(imgUri)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_placeholder))
            .into(imgView)
    }
}

@BindingAdapter("listCard")
fun bindCartView(recyclerView: RecyclerView, data : List<User>?) {

    Log.d("Peter","Here here")



    val adapter = recyclerView.adapter as HomeAdapter


    adapter.submitList(data)
    adapter.notifyDataSetChanged()
}