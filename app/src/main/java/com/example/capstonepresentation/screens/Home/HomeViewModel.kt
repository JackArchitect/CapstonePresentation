package com.example.capstonepresentation.screens.Home

import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import android.app.Application

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val app: Application
) : AndroidViewModel(app) {
}