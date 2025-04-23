package com.example.capstonepresentation.fragments.Home

import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val app: Application
) : AndroidViewModel(app) {
    private val _powerLiveData = MutableLiveData<Double>()
    val powerLiveData: LiveData<Double> = _powerLiveData

    fun updatePower(value: Double) {
        _powerLiveData.value = value
    }
}