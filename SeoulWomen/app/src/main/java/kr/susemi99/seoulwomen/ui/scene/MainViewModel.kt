package kr.susemi99.seoulwomen.ui.scene

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kr.susemi99.seoulwomen.api.Api
import kr.susemi99.seoulwomen.enums.Area
import kr.susemi99.seoulwomen.util.preference.AppPreference
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
  private val api: Api,
  private val appPreference: AppPreference,
) : ViewModel() {
  private val selectedArea = MutableStateFlow(appPreference.area)
  val area: StateFlow<Area> = selectedArea.asStateFlow()

  @OptIn(ExperimentalCoroutinesApi::class)
  val list = selectedArea.flatMapLatest { area ->
    Pager(PagingConfig(pageSize = PAGE_SIZE)) { ItemPagingSource(api, area.className, PAGE_SIZE) }.flow
  }.cachedIn(viewModelScope)

  fun selectArea(area: Area) {
    appPreference.area = area
    selectedArea.value = area
  }
}

private const val PAGE_SIZE = 30
