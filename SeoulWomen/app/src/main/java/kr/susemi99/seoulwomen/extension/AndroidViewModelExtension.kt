package kr.susemi99.seoulwomen.extension

import android.app.Application
import androidx.lifecycle.AndroidViewModel

val AndroidViewModel.context
  get() = getApplication<Application>()