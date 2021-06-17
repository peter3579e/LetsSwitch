package com.peter.letsswtich.question

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.peter.letsswtich.LetsSwtichApplication
import com.peter.letsswtich.R
import com.peter.letsswtich.databinding.ItemAgeSpinnerBinding

class AgeSpinner(private val strings: MutableList<Int>, val indicator: String) : BaseAdapter() {

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding = ItemAgeSpinnerBinding.inflate(LayoutInflater.from(parent?.context),parent,false)

        if(position == 0){
            Log.d("Adapter","$position")
            binding.age = null
            binding.textSpinnerTitle.setTextColor(LetsSwtichApplication.appContext.resources.getColor(R.color.black_12_alpha))
        } else {
            binding.age = strings[position-1]
        }

        return binding.root
    }

    override fun getItem(position: Int): Any {
        return if (position == 0)
            0
        else
            strings[position-1]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
        Log.d("posistion","${getItemId(position)}")
    }

    override fun getCount(): Int {
        return strings.size + 1
    }

}