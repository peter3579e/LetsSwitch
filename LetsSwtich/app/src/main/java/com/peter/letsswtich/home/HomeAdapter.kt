package com.peter.letsswtich.home

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.*
import com.google.android.material.chip.Chip
import com.peter.letsswtich.data.User
import com.peter.letsswtich.databinding.ItemProfilecardBinding
import java.util.*
import kotlin.concurrent.timerTask

class HomeAdapter(val viewModel: HomeViewModel):ListAdapter<User, RecyclerView.ViewHolder> (DiffCallback){



    class UserViewHolder(private var binding: ItemProfilecardBinding): RecyclerView.ViewHolder(binding.root),LifecycleOwner {
        fun bind(user: User,viewModel: HomeViewModel){


            val linearSnapHelper = LinearSnapHelper().apply {
//                attachToRecyclerView(binding.imageCardUser)
                val snapHelper: SnapHelper = PagerSnapHelper()
                binding.imageCardUser.onFlingListener = null
                snapHelper.attachToRecyclerView(binding.imageCardUser)
            }

            binding.lifecycleOwner = this
            binding.item = user
            binding.viewModel = viewModel
            val adapter = ImageAdapter(viewModel)
            val circleAdapter = ImageCircleAdapter()
            binding.imageCardUser.adapter = adapter
            binding.recyclerImageCircles.adapter = circleAdapter

            binding.imageCardUser.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                viewModel.onCampaignScrollChange(
                        binding.imageCardUser.layoutManager,
                        linearSnapHelper
                )
            }

            viewModel.snapPosition.observe(this, androidx.lifecycle.Observer {
                    Log.d("ima","images value = $user")
                    (binding.recyclerImageCircles.adapter as ImageCircleAdapter).selectedPosition.value =
                            (it % (user.personImages.size))

            })

            val layoutManager = binding.imageCardUser.layoutManager

            circleAdapter.submitCount(user.personImages.size)




            binding.cardImagePlus.setOnClickListener {

                    viewModel.snapPosition.value?.let {
                        Log.d("testing","image size = ")
                        if(viewModel.snapPosition.value!!< user.personImages.size-1){
                            layoutManager?.smoothScrollToPosition(
                                    binding.imageCardUser, RecyclerView.State(),
                                    it.plus(1)

                            )
                        }
                        Log.d("timer", "position ${it}")
                    }
            }

            binding.cardImageMinus.setOnClickListener {
                    viewModel.snapPosition.value?.let {
                        if (viewModel.snapPosition.value!! > 0) {
                            layoutManager?.smoothScrollToPosition(
                                    binding.imageCardUser, RecyclerView.State(),
                                    it.minus(1)
                            )
                        }
                        Log.d("timer", "position ${it}")
                    }
            }

            val chipGroup = binding.chipGroup

            var language = user.fluentLanguage

            if (viewModel.count){
                language = emptyList()
            }

            Log.d("HomeAdapter","Adapter has run")

            for (language in language){
                val chip = Chip(chipGroup.context)
                chip.text = language
                chipGroup.addView(chip)
            }
            // This is important, because it forces the data binding to execute immediately,
            // which allows the RecyclerView to make the correct view size measurements
            binding.executePendingBindings()
        }

        private val lifecycleRegistry = LifecycleRegistry(this)

        init {
            lifecycleRegistry.currentState = Lifecycle.State.INITIALIZED
        }

        fun onAttach() {
            lifecycleRegistry.currentState = Lifecycle.State.STARTED
        }

        fun onDetach() {
            lifecycleRegistry.currentState = Lifecycle.State.CREATED
        }

        override fun getLifecycle(): Lifecycle {
            return lifecycleRegistry
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_USER -> UserViewHolder(ItemProfilecardBinding.inflate(
                LayoutInflater.from(parent.context), parent, false))
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is UserViewHolder -> {
                holder.bind(getItem(position) as User,viewModel)

                val user = getItem(position) as User
//                viewModel.userPersonImage.value = user.personImages

//                Log.d("HomeAdapter","User personImages value ${viewModel.userPersonImage.value}")

            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return ITEM_VIEW_TYPE_USER
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        when (holder) {
            is UserViewHolder -> holder.onAttach()
        }
    }

    /**
     * It for [LifecycleRegistry] change [onViewDetachedFromWindow]
     */
    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        when (holder) {
            is UserViewHolder -> holder.onDetach()
        }
    }



    companion object DiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem === newItem
        }
        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        private const val ITEM_VIEW_TYPE_USER = 0x00
    }



}

