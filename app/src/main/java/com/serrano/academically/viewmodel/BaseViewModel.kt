package com.serrano.academically.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.serrano.academically.api.DrawerData
import com.serrano.academically.utils.ProcessState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

open class BaseViewModel(application: Application): AndroidViewModel(application) {

    protected val mutableProcessState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = mutableProcessState.asStateFlow()

    protected val mutableDrawerData = MutableStateFlow(DrawerData())
    val drawerData: StateFlow<DrawerData> = mutableDrawerData.asStateFlow()

}