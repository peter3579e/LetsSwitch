package com.peter.letsswtich.question

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.common.primitives.UnsignedBytes.toInt
import com.peter.letsswtich.LetsSwtichApplication
import com.peter.letsswtich.LoginNavigationDirections
import com.peter.letsswtich.NavigationDirections
import com.peter.letsswtich.R
import com.peter.letsswtich.data.User
import com.peter.letsswtich.databinding.FragmentFirstQuestionnaireBinding
import com.peter.letsswtich.ext.getVmFactory
import com.peter.letsswtich.login.UserManager
import com.peter.letsswtich.util.Logger


class FirstQuestionnaireFragment:Fragment() {

    private lateinit var binding: FragmentFirstQuestionnaireBinding

    private val viewModel : FirstQuestionnaireViewModel by viewModels<FirstQuestionnaireViewModel> { getVmFactory() }

//    override fun onStart() {
//        super.onStart()
//
//        viewModel.getUserDetail(UserManager.user.email)
//
//        viewModel.userDetail.observe(viewLifecycleOwner, Observer {
//
//            val userInfo = viewModel.userDetail.value!!
//
//            Log.d("FirstQuestion","the value of user from firebase = $userInfo")
//
//        })
//
//
//        Log.d("FirstQuestion","${UserManager.user}")
//
//        Log.d("FirstQuestion","Run1")
//
//
//        Log.d("FirstQuestion","Run3")
//
//    }


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFirstQuestionnaireBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        Logger.d("Run1")

        val ageIndicator = LetsSwtichApplication.instance.resources.getString(R.string.spinner_select_age)
        val genderIndicator = LetsSwtichApplication.instance.resources.getString(R.string.spinner_select_gender)
        val mothertongue = LetsSwtichApplication.instance.resources.getString(R.string.spinner_select_mothertongue)
        val fluentLanguage = LetsSwtichApplication.instance.resources.getString(R.string.spinner_select_fluent)
        val genderContent = LetsSwtichApplication.instance.resources.getStringArray(R.array.gender_array)
        val languageContent = LetsSwtichApplication.instance.resources.getStringArray(R.array.language_array)
        Logger.d("Run2")

        val ageArray:MutableList<Int> = mutableListOf<Int>()
        Logger.d("Run3")
        var count = 0
        Logger.d("Run4")
        for (i in 0..99){
            count ++
            ageArray.add(count)
        }
        Logger.d("Run5")
//        Log.d("FirstQuestion","the value of array = ${ageArray[2]}")

        Logger.d("Run6")

        binding.age.adapter = AgeSpinner(ageArray,ageIndicator)
        binding.gender.adapter = SpinnerAdapter(genderContent,genderIndicator)
        binding.mothertongue.adapter = SpinnerAdapter(languageContent,mothertongue)
        binding.fluent.adapter = SpinnerAdapter(languageContent,fluentLanguage)

        Logger.d("Run7")

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
            Log.d("chose value","value = $it")
        })

        viewModel.selectedGender.observe(viewLifecycleOwner, Observer {
            Log.d("chose value","value = $it")
        })

        viewModel.selectedMothertongue.observe(viewLifecycleOwner, Observer {
            Log.d("chose value","value = $it")
        })

        viewModel.selectedFluent.observe(viewLifecycleOwner, Observer {
            Log.d("chose value","value = $it")
        })


        binding.buttonNext.setOnClickListener {
            if (isFinished()) {


                Log.d("FirstQuestion","the value of UserManager = ${UserManager.user}")


                UserManager.user.age = viewModel.selectedAge.value!!
                UserManager.user.gender = viewModel.selectedGender.value!!
                UserManager.user.fluentLanguage = listOf(viewModel.selectedMothertongue.value!!,viewModel.selectedFluent.value!!)

                Log.d("FirstQuestion","the value of UserManager = ${UserManager.user}")

                findNavController().navigate(LoginNavigationDirections.navigateToSecondQuestionnaire())

                viewModel.postUser(UserManager.user)
            }
        }


//        binding.buttonNext.setOnClickListener {
//            if (isFinished()) {
//                navigateToNext()
//            }
//        }
//
        return binding.root
    }

    private fun isFinished(): Boolean {

        return when {
            viewModel.selectedAge.value != null && viewModel.selectedFluent.value != null &&
                    viewModel.selectedGender.value != null && viewModel.selectedMothertongue.value != null ->{
                Toast.makeText(LetsSwtichApplication.appContext, getString(R.string.select_requiremnt), Toast.LENGTH_SHORT).show()
                true
            }


            else -> {
                Toast.makeText(LetsSwtichApplication.appContext, getString(R.string.remindertofillInfor), Toast.LENGTH_SHORT).show()
                false
            }
        }

    }
}