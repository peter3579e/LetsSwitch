package com.peter.letsswtich.setting

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.slider.RangeSlider
import com.peter.letsswtich.*
import com.peter.letsswtich.data.Requirement
import com.peter.letsswtich.databinding.FragmentSettingBinding
import com.peter.letsswtich.editprofile.EditSpinnerAdapter
import com.peter.letsswtich.ext.getVmFactory
import com.peter.letsswtich.login.UserManager
import com.peter.letsswtich.profile.ProfileFragmentArgs
import com.peter.letsswtich.profile.ProfileViewModel
import com.peter.letsswtich.question.AgeSpinner
import com.peter.letsswtich.question.SpinnerAdapter
import com.peter.letsswtich.util.Logger

class SettingFragment:Fragment() {

    private lateinit var binding: FragmentSettingBinding

    private val viewModel by viewModels<SettingViewModel> {
        getVmFactory(
                SettingFragmentArgs.fromBundle(requireArguments()).selectedAnswer
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSettingBinding.inflate(inflater,container,false)
        binding.viewModel = viewModel

        val mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        Log.d("SettingFragment","main = ${mainViewModel.requirment.value}")


        viewModel.profile.observe(viewLifecycleOwner, Observer {profile ->
            Log.d("SettingFragment","the value of need = ${viewModel.need}")
                if (profile == true && isFinished() ){


//                Log.d("SecondQuestion", "the value of UserManager = ${UserManager.user}")


                    Log.d("SettingFragment", "the requirement = ${viewModel.need}")

                    viewModel.postRequirement(UserManager.user.email,viewModel.need)
                    mainViewModel.userdetail.value!!.preferLanguage = listOf(viewModel.need.language)
                    UserManager.user.preferLanguage = listOf(viewModel.need.language)
//                    mainViewModel.requirment.value = requirement


                    Log.d("settingFragment", "the value of requirement = ${UserManager.user}")

                    findNavController().navigate(NavigationDirections.navigateToProfileFragment(mainViewModel.userdetail.value!!,false))
                    viewModel.profileNavigated()
                }
        })

        val genderIndicator = viewModel.need.gender
        val cityIndicator = viewModel.need.city
        val mothertongue = viewModel.need.language
        val genderContent =
                LetsSwtichApplication.instance.resources.getStringArray(R.array.gender_array)
        val languageContent =
                LetsSwtichApplication.instance.resources.getStringArray(R.array.language_array)

        val cityContent = LetsSwtichApplication.instance.resources.getStringArray(R.array.city_array)


        binding.genderSpinner.adapter = EditSpinnerAdapter(genderContent, genderIndicator)
        binding.languageSpinner.adapter = EditSpinnerAdapter(languageContent, mothertongue)
        binding.citySpinner.adapter = EditSpinnerAdapter(cityContent, cityIndicator)



        binding.ageSlider.addOnSliderTouchListener(

                object : RangeSlider.OnSliderTouchListener {

                    override fun onStartTrackingTouch(slider: RangeSlider) {
                        viewModel.minAge.value = slider.values[0].toInt()
                        viewModel.maxAge.value = slider.values[1].toInt()
                        binding.maxAge = slider.values[1].toString()
                        binding.minAge = slider.values[0].toString()
                        Log.d("onStartTrackingTouch", "${slider.values}")

                    }

                    override fun onStopTrackingTouch(slider: RangeSlider) {
                        viewModel.minAge.value = slider.values[0].toInt()
                        viewModel.maxAge.value = slider.values[1].toInt()
                        binding.maxAge = slider.values[1].toString()
                        binding.minAge = slider.values[0].toString()
                        viewModel.need.age = listOf(slider.values[0].toInt(),slider.values[1].toInt())
                        Log.d("onStopTrackingTouch min", "${viewModel.minAge.value}")
                        Log.d("onStopTrackingTouch min", "${viewModel.maxAge.value}")
                    }
                })


        binding.genderSpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(p0: AdapterView<*>?) {
                    }

                    override fun onItemSelected(
                            parent: AdapterView<*>?, view: View?, pos: Int, id: Long
                    ) {

                        Log.d("settingFragment","position value = $pos")
                        if (parent !=null && pos == 0){
                            viewModel.selectedGender.value = ""
                        }

                        if (parent != null && pos != 0) {
                            viewModel.setupGender(parent.selectedItem.toString())
                        }

                    }
                }

        binding.languageSpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(p0: AdapterView<*>?) {
                    }

                    override fun onItemSelected(
                            parent: AdapterView<*>?, view: View?, pos: Int, id: Long
                    ) {

                        if (parent != null && pos != 0) {
                            viewModel.setupMothertongue(parent.selectedItem.toString())
                        }

                    }
                }

        binding.citySpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(p0: AdapterView<*>?) {
                    }

                    override fun onItemSelected(
                            parent: AdapterView<*>?, view: View?, pos: Int, id: Long
                    ) {

                        if (parent != null && pos != 0) {
                            viewModel.setupFluent(parent.selectedItem.toString())
                        }

                    }
                }

        viewModel.selectedGender.observe(viewLifecycleOwner, Observer {
            viewModel.need.gender = it
            Log.d("settingFragment", "value = ${viewModel.need.gender}")
        })

        viewModel.selectedFirstLanguage.observe(viewLifecycleOwner, Observer {
           viewModel.need.language = it
            Log.d("settingFragment", "value = ${viewModel.need.gender}")
        })

        viewModel.city.observe(viewLifecycleOwner, Observer {
            viewModel.need.city = it
            Log.d("settingFragment", "value = ${viewModel.need.gender}")
        })


        return binding.root
    }

    private fun isFinished(): Boolean {

        return when {
            viewModel.need.age.isNotEmpty() && viewModel.need.city != ""  -> {
                true
            }


            else -> {
                Toast.makeText(
                        LetsSwtichApplication.appContext,
                        getString(R.string.remindertofillInfor),
                        Toast.LENGTH_SHORT
                ).show()
                false
            }
        }

    }


}