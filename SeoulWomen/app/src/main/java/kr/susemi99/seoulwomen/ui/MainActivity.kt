package kr.susemi99.seoulwomen.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import kr.susemi99.seoulwomen.R
import kr.susemi99.seoulwomen.api.WomenService
import kr.susemi99.seoulwomen.application.App
import kr.susemi99.seoulwomen.databinding.MainActivityBinding
import kr.susemi99.seoulwomen.util.AppPreference

class MainActivity : AppCompatActivity() {
  private lateinit var binding: MainActivityBinding

  companion object {
    const val OFFSET = 20
  }

  private val classListAdapter = ClassListAdapter()
  private var startIndex = 1
  private var endIndex = OFFSET
  private val disposableBag = CompositeDisposable()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = MainActivityBinding.inflate(layoutInflater).also {
      setContentView(it.root)
    }

    setSupportActionBar(binding.toolbar)

    val toggle = ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
    binding.drawerLayout.addDrawerListener(toggle)
    toggle.syncState()

    binding.navView.setNavigationItemSelectedListener { menuItem ->
      binding.drawerLayout.closeDrawers()
      AppPreference.lastSelectedAreaNameV2 = menuItem.title.toString()
      AppPreference.lastSelectedAreaValue = menuItem.titleCondensed.toString()
      reset()
      true
    }

    binding.classListView.apply {
      adapter = classListAdapter

      val currentLayoutManager = layoutManager as LinearLayoutManager
      val decoration = DividerItemDecoration(App.instance, currentLayoutManager.orientation)
      addItemDecoration(decoration)
      addOnScrollListener(object : EndlessRecyclerViewScrollListener(OFFSET, currentLayoutManager) {
        override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
          startIndex = endIndex + 1
          endIndex += OFFSET
          loadData()
        }
      })
    }

    loadData()
  }

  override fun onBackPressed() {
    if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
      binding.drawerLayout.closeDrawers()
    } else {
      super.onBackPressed()
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    disposableBag.clear()
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.main_menu, menu)
    return super.onCreateOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    if (R.id.refreshMenuItem == item.itemId) {
      reset()
    }

    return super.onOptionsItemSelected(item)
  }

  private fun reset() {
    startIndex = 1
    endIndex = OFFSET
    binding.errorLabel.isGone = true
    classListAdapter.clear()
    disposableBag.clear()

    loadData()
  }

  private fun loadData() {
    title = AppPreference.lastSelectedAreaNameV2

    WomenService.list(startIndex, endIndex)
      .subscribe(
        {
          if (it.result == null) {
            classListAdapter.addAll(it.classItem.rows)
          }

          if (classListAdapter.itemCount == 0) {
            displayErrorLabel(it.result?.message)
          }
        },
        {
          if (classListAdapter.itemCount == 0) {
            displayErrorLabel(it.localizedMessage)
          }
        }
      ).addTo(disposableBag)
  }

  private fun displayErrorLabel(string: String? = getString(R.string.no_result)) {
    binding.errorLabel.apply {
      text = string
      isVisible = true
    }
  }
}
