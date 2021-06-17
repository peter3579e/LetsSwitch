package com.peter.letsswtich.editprofile

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.peter.letsswtich.editprofile.preview.PreviewFragment

class ViewpagerAdapeter(fragmentManager: FragmentManager, viewModel: EditProfileViewModel) : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private var viewModel = viewModel

    override fun getCount() = EditProfileFilter.values().size

    override fun getPageTitle(position: Int): CharSequence? {
        return EditProfileFilter.values()[position].value
    }

    override fun getItem(position: Int): Fragment {
        return when(position){
            0 -> EditFragment(viewModel.user)
            else -> PreviewFragment(viewModel.user)
        }
    }
}