package com.peter.letsswtich

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.Shape
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.peter.letsswtich.chat.NewMatchedAdapter
import com.peter.letsswtich.data.User
import com.peter.letsswtich.editprofile.preview.PreviewImageAdapter
import com.peter.letsswtich.home.HomeAdapter
import com.peter.letsswtich.home.ImageAdapter
import com.peter.letsswtich.home.ImageCircleAdapter
import com.peter.letsswtich.map.FriendsImageAdapter
import com.peter.letsswtich.profile.PhotosAdapter
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

@BindingAdapter("itemPosition", "itemCount")
fun setupPaddingForGridItems(layout: ConstraintLayout, position: Int, count: Int) {

    val outsideHorizontal = LetsSwtichApplication.instance.resources.getDimensionPixelSize(R.dimen.space_outside_horizontal_photo_item)
    val insideHorizontal = LetsSwtichApplication.instance.resources.getDimensionPixelSize(R.dimen.space_inside_horizontal_photo_item)
    val outsideVertical = LetsSwtichApplication.instance.resources.getDimensionPixelSize(R.dimen.space_outside_vertical_photo_item)
    val insideVertical = LetsSwtichApplication.instance.resources.getDimensionPixelSize(R.dimen.space_inside_vertical_photo_item)

    val layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)

    when {
        position == 0 -> { // first item and confirm whether only 1 row
            layoutParams.setMargins(outsideHorizontal, outsideVertical, insideHorizontal, if (count > 2) insideVertical else outsideVertical)
        }
        position == 1 -> { // second item and confirm whether only 1 row
            layoutParams.setMargins(insideHorizontal, outsideVertical, outsideHorizontal, if (count > 2) insideVertical else outsideVertical)
        }
        count % 2 == 0 && position == count - 1 -> { // count more than 2 and item count is even
            layoutParams.setMargins(insideHorizontal, insideVertical, outsideHorizontal, outsideVertical)
        }
        (count % 2 == 1 && position == count - 1) || (count % 2 == 0 && position == count - 2) -> {
            layoutParams.setMargins(outsideHorizontal, insideVertical, insideHorizontal, outsideVertical)
        }
        position % 2 == 0 -> { // even
            when (position) {
                count - 1 -> layoutParams.setMargins(insideHorizontal, insideVertical, outsideHorizontal, outsideVertical) // last 1
                count - 2 -> layoutParams.setMargins(outsideHorizontal, insideVertical, insideHorizontal, outsideVertical) // last 2
                else -> layoutParams.setMargins(outsideHorizontal, insideVertical, insideHorizontal, insideVertical)
            }
        }
        position % 2 == 1 -> { // odd
            when (position) {
                count - 1 -> layoutParams.setMargins(outsideHorizontal, insideVertical, insideHorizontal, outsideVertical) // last 1
                else -> layoutParams.setMargins(insideHorizontal, insideVertical, outsideHorizontal, insideVertical)
            }
        }
    }

    layout.layoutParams = layoutParams
}

//@BindingAdapter("userImages")
//fun bindRecyclerViewWithUsersImages(recyclerView: RecyclerView, users: List<String>?) {
//    users?.let {
//        recyclerView.adapter?.apply {
//            when (this) {
//                is FriendsImageAdapter -> {
//                    submitImages(it)
//                }
//
//            }
//        }
//    }
//}

@BindingAdapter("images")
fun bindRecyclerViewWithImages(recyclerView: RecyclerView, images: List<String>?) {
    images?.let {
        recyclerView.adapter?.apply {
            when (this) {
                is ImageAdapter -> {
                    submitImages(it)
                }
                is PreviewImageAdapter -> {
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
