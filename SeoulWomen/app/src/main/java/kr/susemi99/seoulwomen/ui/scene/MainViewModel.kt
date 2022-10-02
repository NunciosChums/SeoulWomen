package kr.susemi99.seoulwomen.ui.scene

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
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
  application: Application,
  private val api: Api,
  private val appPreference: AppPreference,
) : AndroidViewModel(application) {
  var title by mutableStateOf(appPreference.areaTitle)
  private var areaClassName by mutableStateOf(appPreference.areaClassName)
  val list = Pager(PagingConfig(pageSize = 3)) { ItemPagingSource(api, areaClassName) }.flow.cachedIn(viewModelScope)

  init {
    if (appPreference.areaTitle.isBlank()) {
      selectedArea(Area.values().first())
    }
  }

  fun selectedArea(area: Area, onChanged: (() -> Unit)? = null) {
    with(appPreference) {
      areaTitle = area.title
      areaClassName = area.className
    }
    title = area.title
    areaClassName = area.className
    onChanged?.invoke()
  }
}