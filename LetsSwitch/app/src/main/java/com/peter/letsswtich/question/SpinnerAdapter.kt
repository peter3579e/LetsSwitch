package com.peter.letsswtich.question

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.peter.letsswtich.LetsSwtichApplication
import com.peter.letsswtich.MainViewModel
import com.peter.letsswtich.R
import com.peter.letsswtich.databinding.ItemMyquestionSpinnerBinding
import com.peter.letsswtich.util.CurrentFragmentType

class SpinnerAdapter(private val strings: Array<String>, private val indicator: String) :
    BaseAdapter() {


    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding = ItemMyquestionSpinnerBinding.inflate(
            LayoutInflater.from(parent?.context),
            parent,
            false
        )

        if (position == 0) {
            binding.user = indicator
            binding.textSpinnerTitle.setTextColor(
                LetsSwtichApplication.appContext.resources.getColor(
                    R.color.black_12_alpha
                )
            )

        } else {
            binding.user = strings[position - 1]
        }

        return binding.root
    }

    override fun getItem(position: Int): Any {
        return if (position == 0)
            0
        else
            strings[position - 1]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return strings.size + 1
    }

}