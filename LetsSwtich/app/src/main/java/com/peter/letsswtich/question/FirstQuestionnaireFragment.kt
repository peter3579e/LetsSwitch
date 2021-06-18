package com.peter.letsswtich.question

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
import androidx.navigation.fragment.findNavController
import com.peter.letsswtich.LetsSwtichApplication
import com.peter.letsswtich.LoginNavigationDirections
import com.peter.letsswtich.R
import com.peter.letsswtich.databinding.FragmentFirstQuestionnaireBinding
import com.peter.letsswtich.ext.getVmFactory
import com.peter.letsswtich.login.UserManager
import com.peter.letsswtich.util.Logger


class FirstQuestionnaireFragment : Fragment() {

    private lateinit var binding: FragmentFirstQuestionnaireBinding

    private val viewModel: FirstQuestionnaireViewModel by viewModels<FirstQuestionnaireViewModel> { getVmFactory() }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFirstQuestionnaireBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        Logger.d("Run1")

        val ageIndicator =
            LetsSwtichApplication.instance.resources.getString(R.string.spinner_select_age)
        val genderIndicator =
            LetsSwtichApplication.instance.resources.getString(R.string.spinner_select_gender)
        val mothertongue =
            LetsSwtichApplication.instance.resources.getString(R.string.spinner_select_mothertongue)
        val fluentLanguage =
            LetsSwtichApplication.instance.resources.getString(R.string.spinner_select_fluent)
        val cityIndicator =
            LetsSwtichApplication.instance.resources.getString(R.string.spinner_select_city)
        val genderContent =
            LetsSwtichApplication.instance.resources.getStringArray(R.array.gender_array)
        val languageContent =
            LetsSwtichApplication.instance.resources.getStringArray(R.array.language_array)
        val cityContent =
            LetsSwtichApplication.instance.resources.getStringArray(R.array.city_array)

        val ageArray: MutableList<Int> = mutableListOf<Int>()
        var count = 0
        for (i in 0..99) {
            count++
            ageArray.add(count)
        }

        binding.age.adapter = AgeSpinner(ageArray, ageIndicator)
        binding.gender.adapter = SpinnerAdapter(genderContent, genderIndicator)
        binding.mothertongue.adapter = SpinnerAdapter(languageContent, mothertongue)
        binding.fluent.adapter = SpinnerAdapter(languageContent, fluentLanguage)
        //Setup Spinner
        binding.city.adapter = SpinnerAdapter(cityContent, cityIndicator)

        binding.city.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, pos: Int, id: Long
                ) {

                    if (parent != null && pos != 0) {
                        viewModel.setupCity(parent.selectedItem.toString())
                    }

                }
            }

        viewModel.selectedCity.observe(viewLifecycleOwner, Observer {
            Logger.d(it.toString())
        })


        binding.age.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, pos: Int, id: Long
                ) {

                    if (parent != null && pos != 0) {
                        viewModel.setupAge(parent.selectedItem as Int)
                    }

                }
            }

        binding.gender.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, pos: Int, id: Long
                ) {

                    if (parent != null && pos != 0) {
                        viewModel.setupGender(parent.selectedItem.toString())
                    }

                }
            }

        binding.mothertongue.onItemSelectedListener =
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

        binding.fluent.onItemSelectedListener =
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

        viewModel.selectedAge.observe(viewLifecycleOwner, Observer {
            Log.d("chose value", "value = $it")
        })

        viewModel.selectedGender.observe(viewLifecycleOwner, Observer {
            Log.d("chose value", "value = $it")
        })

        viewModel.selectedMothertongue.observe(viewLifecycleOwner, Observer {
            Log.d("chose value", "value = $it")
        })

        viewModel.selectedFluent.observe(viewLifecycleOwner, Observer {
            Log.d("chose value", "value = $it")
        })


        binding.buttonNext.setOnClickListener {
            if (isFinished()) {


                Log.d("FirstQuestion", "the value of UserManager = ${UserManager.user}")


                UserManager.user.age = viewModel.selectedAge.value!!
                UserManager.user.gender = viewModel.selectedGender.value!!
                UserManager.user.fluentLanguage =
                    listOf(viewModel.selectedMothertongue.value!!, viewModel.selectedFluent.value!!)
                UserManager.user.city = viewModel.selectedCity.value!!

                Log.d("FirstQuestion", "the value of UserManager = ${UserManager.user}")

                findNavController().navigate(LoginNavigationDirections.navigateToSecondQuestionnaire())

                viewModel.postUser(UserManager.user)
            }
        }


        return binding.root
    }

    private fun isFinished(): Boolean {

        return when {
            viewModel.selectedAge.value != null && viewModel.selectedFluent.value != null &&
                    viewModel.selectedGender.value != null && viewModel.selectedMothertongue.value != null && viewModel.selectedCity.value != null -> {
                Toast.makeText(
                    LetsSwtichApplication.appContext,
                    getString(R.string.select_requiremnt),
                    Toast.LENGTH_SHORT
                ).show()
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