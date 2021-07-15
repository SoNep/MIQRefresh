package nz.non.miqrefresh.scan.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import nz.non.miqrefresh.scan.uc.GetRefreshUseCase
import nz.non.miqrefresh.scan.ui.MainUIO
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(var getRefreshUseCase: GetRefreshUseCase) : ViewModel() {

    private val liveData = MutableLiveData<MainUIO>()

    fun startScan(isApi: Boolean = true): LiveData<MainUIO> {
        viewModelScope.launch {
            val resultFlow =
                if (isApi) getRefreshUseCase.startRefreshApi() else getRefreshUseCase.startRefreshMiq()
            resultFlow.collect { element ->
                liveData.value =
                    MainUIO(
                        element.arrivalDatesString,
                        element.receivedAt,
                        element.progress.toString()
                    )
            }
        }
        return liveData
    }

    fun stopScan() {
        viewModelScope.launch {
            getRefreshUseCase.stopRefresh()
        }
    }
}


