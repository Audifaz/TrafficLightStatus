package com.audifaz.trafficLightStatus.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Waiting"
    }

    fun setText(value: String){
        _text.postValue(value)
    }

    val text: LiveData<String> = _text
}