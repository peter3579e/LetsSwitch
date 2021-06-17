package com.peter.letsswtich.question

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
import com.google.android.material.slider.RangeSlider
import com.peter.letsswtich.*
import com.peter.letsswtich.data.Requirement
import com.peter.letsswtich.databinding.FragmentSecondQuestionnaireBinding
import com.peter.letsswtich.ext.getVmFactory
import com.peter.letsswtich.login.UserManager

class SecondQutionnaireFragment : Fragment() {

    private lateinit var binding: FragmentSecondQuestionnaireBinding

    private val viewModel: SecondQuestionnaireViewModel by viewModels<SecondQuestionnaireViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSecondQuestionnaireBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        binding.maxAge = 70
        binding.minAge = 20

        val genderIndicator =
            LetsSwtichApplication.instance.resources.getString(R.string.spinner_select_gender)
        val cityIndicator =
            LetsSwtichApplication.instance.resources.getString(R.string.spinner_select_city)
        val mothertongue =
            LetsSwtichApplication.instance.resources.getString(R.string.spinner_select_mothertongue)
        val fluentLanguage =
            LetsSwtichApplication.instance.resources.getString(R.string.spinner_select_fluent)
        val genderContent =
            LetsSwtichApplication.instance.resources.getStringArray(R.array.gender_array)
        val languageContent =
            LetsSwtichApplication.instance.resources.getStringArray(R.array.language_array)

        val cityContent =
            LetsSwtichApplication.instance.resources.getStringArray(R.array.city_array)


        binding.gender.adapter = SpinnerAdapter(genderContent, genderIndicator)
        binding.preferFirst.adapter = SpinnerAdapter(languageContent, mothertongue)
        binding.city.adapter = SpinnerAdapter(cityContent, cityIndicator)



        binding.ageSlider.addOnSliderTouchListener(

            object : RangeSlider.OnSliderTouchListener {

                override fun onStartTrackingTouch(slider: RangeSlider) {
                    Log.d("onStartTrackingTouch", "${slider.values}")

                }

                override fun onStopTrackingTouch(slider: RangeSlider) {
                    viewModel.minAge.value = slider.values[0].toInt()
                    viewModel.maxAge.value = slider.values[1].toInt()
                    binding.maxAge = slider.values[1].toInt()
                    binding.minAge = slider.values[0].toInt()
                    Log.d("onStopTrackingTouch min", "${viewModel.minAge.value}")
                    Log.d("onStopTrackingTouch min", "${viewModel.maxAge.value}")
                }
            })


        binding.gender.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, pos: Int, id: Long
                ) {

                    Log.d("test", "position value = $pos")
                    if (parent != null && pos == 0) {
                        viewModel.selectedGender.value = ""
                    }

                    if (parent != null && pos != 0) {
                        viewModel.setupGender(parent.selectedItem.toString())
                    }

                }
            }

        binding.preferFirst.onItemSelectedListener =
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

        binding.city.onItemSelectedListener =
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
            Log.d("chose value", "value = $it")
        })

        viewModel.selectedFirstLanguage.observe(viewLifecycleOwner, Observer {
            Log.d("chose value", "value = $it")
        })

        viewModel.city.observe(viewLifecycleOwner, Observer {
            Log.d("chose value", "value = $it")
        })

        binding.buttonNext.setOnClickListener {
            if (isFinished()) {


//                Log.d("SecondQuestion", "the value of UserManager = ${UserManager.user}")


                val requirement = Requirement(
                    gender = viewModel.selectedGender.value.toString(),
                    age = listOf(viewModel.minAge.value!!, viewModel.maxAge.value!!),
                    city = viewModel.city.value!!,
                    language = viewModel.selectedFirstLanguage?.value!!
                )


                viewModel.postRequirement(UserManager.user.email, requirement)

                startActivity(Intent(context, MainActivity::class.java))
                requireActivity().finish()



                Log.d("SecondQuestion", "the value of requirement = ${requirement}")


            }
        }





        return binding.root
    }

    private fun isFinished(): Boolean {

        return when {
            viewModel.maxAge.value != null && viewModel.minAge.value != null && viewModel.selectedFirstLanguage.value != null && viewModel.city.value != null -> {
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