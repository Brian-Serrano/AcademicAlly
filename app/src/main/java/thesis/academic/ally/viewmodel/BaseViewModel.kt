package thesis.academic.ally.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import thesis.academic.ally.api.DrawerData
import thesis.academic.ally.utils.ProcessState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

open class BaseViewModel(application: Application): AndroidViewModel(application) {

    protected val mutableProcessState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = mutableProcessState.asStateFlow()

    protected val mutableDrawerData = MutableStateFlow(DrawerData())
    val drawerData: StateFlow<DrawerData> = mutableDrawerData.asStateFlow()

}