package com.peter.letsswtich

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.Shape
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.peter.letsswtich.chat.NewMatchedAdapter
import com.peter.letsswtich.data.User
import com.peter.letsswtich.home.HomeAdapter
import com.peter.letsswtich.home.ImageAdapter
import com.peter.letsswtich.home.ImageCircleAdapter
import com.peter.letsswtich.util.TimeUtil
import com.peter.letsswtich.util.Util.getColor

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

@BindingAdapter("circleStatus")
fun bindDetailCircleStatus(imageView: ImageView, isSelected: Boolean = false) {
    imageView.background = ShapeDrawable(object : Shape() {
        override fun draw(canvas: Canvas, paint: Paint) {

            paint.color = getColor(R.color.white)
            paint.isAntiAlias = true

            when (isSelected) {
                true -> {
                    paint.style = Paint.Style.FILL
                }
                false -> {
                    paint.style = Paint.Style.STROKE
                    paint.strokeWidth = LetsSwtichApplication.instance.resources
                            .getDimensionPixelSize(R.dimen.edge_detail_circle).toFloat()
                }
            }

            canvas.drawCircle(this.width / 2, this.height / 2,
                    LetsSwtichApplication.instance.resources
                            .getDimensionPixelSize(R.dimen.radius_detail_circle).toFloat(), paint)
        }
    })
}

@BindingAdapter("addDecoration")
fun bindDecoration(recyclerView: RecyclerView, decoration: RecyclerView.ItemDecoration?) {
    decoration?.let { recyclerView.addItemDecoration(it) }
}

@BindingAdapter("count")
fun bindRecyclerViewByCount(recyclerView: RecyclerView, count: Int?) {
    count?.let {
        recyclerView.adapter?.apply {
            when (this) {
                is ImageCircleAdapter -> {
                    submitCount(it)
                }
            }
        }
    }
}

@BindingAdapter("images")
fun bindRecyclerViewWithImages(recyclerView: RecyclerView, images: List<String>?) {
    images?.let {
        recyclerView.adapter?.apply {
            when (this) {
                is ImageAdapter -> {
                    submitImages(it)
                }
            }
        }
    }
}

@BindingAdapter("listCard")
fun bindCartView(recyclerView: RecyclerView, data : List<User>?) {

    Log.d("Peter","Here here")



    val adapter = recyclerView.adapter as HomeAdapter


    adapter.submitList(data)
    adapter.notifyDataSetChanged()
}

@BindingAdapter("ago")
fun bindAgo(textView: TextView, time:Long?){
    time?.let { textView.text = TimeUtil.stampToAgo(time) }
}
