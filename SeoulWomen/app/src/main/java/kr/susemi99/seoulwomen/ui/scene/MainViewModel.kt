package kr.susemi99.seoulwomen.ui.scene

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kr.susemi99.seoulwomen.api.Api
import kr.susemi99.seoulwomen.enums.Area
import kr.susemi99.seoulwomen.util.preference.AppPreference
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
  private val api: Api,
  private val appPreference: AppPreference,
) : ViewModel() {
  var title by mutableStateOf(appPreference.areaTitle)
    private set
  private var areaClassName = appPreference.areaClassName
  val list = Pager(PagingConfig(pageSize = PAGE_SIZE)) { ItemPagingSource(api, areaClassName, PAGE_SIZE) }.flow.cachedIn(viewModelScope)

  init {
    if (appPreference.areaTitle.isBlank()) {
      selectedArea(Area.entries.first())
    }
  }

  fun selectedArea(area: Area) {
    with(appPreference) {
      areaTitle = area.title
      areaClassName = area.className
    }
    title = area.title
    areaClassName = area.className
  }
}

private const val PAGE_SIZE = 30
