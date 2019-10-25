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
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.main_activity.*
import kotlinx.android.synthetic.main.main_app_bar.*
import kotlinx.android.synthetic.main.main_content.*
import kr.susemi99.seoulwomen.R
import kr.susemi99.seoulwomen.application.App
import kr.susemi99.seoulwomen.network.WomenService
import kr.susemi99.seoulwomen.util.AppPreference

class MainActivity : AppCompatActivity() {
  companion object {
    const val OFFSET = 20
  }

  private val classListAdapter = ClassListAdapter()
  private var startIndex = 1
  private var endIndex = OFFSET
  private val disposable = CompositeDisposable()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.main_activity)
    setSupportActionBar(toolbar)

    val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
    drawerLayout.addDrawerListener(toggle)
    toggle.syncState()

    navView.setNavigationItemSelectedListener { menuItem ->
      drawerLayout.closeDrawers()
      AppPreference.lastSelectedAreaName = menuItem.title.toString()
      AppPreference.lastSelectedAreaValue = menuItem.titleCondensed.toString()
      reset()
      true
    }

    classListView.apply {
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
    if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
      drawerLayout.closeDrawers()
    } else {
      super.onBackPressed()
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    disposable.clear()
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
    errorLabel.isGone = true
    classListAdapter.clear()
    disposable.clear()

    loadData()
  }

  private fun loadData() {
    title = "${AppPreference.lastSelectedAreaName} 여성인력 개발센터"

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
      ).addTo(disposable)
  }

  private fun displayErrorLabel(string: String? = getString(R.string.no_result)) {
    errorLabel.apply {
      text = string
      isVisible = true
    }
  }
}
