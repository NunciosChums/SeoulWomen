package kr.susemi99.seoulwomen.ui.scene

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import kotlinx.coroutines.launch
import kr.susemi99.seoulwomen.R
import kr.susemi99.seoulwomen.api.ApiException
import kr.susemi99.seoulwomen.enums.Area
import kr.susemi99.seoulwomen.ui.theme.RowTitleColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScene() {
  val viewModel = viewModel<MainViewModel>()
  val scrollState = rememberLazyListState()
  val listItems = viewModel.list.collectAsLazyPagingItems()
  val scope = rememberCoroutineScope()
  val context = LocalContext.current
  val drawerState = rememberDrawerState(DrawerValue.Closed)

  fun closeDrawer() {
    scope.launch { drawerState.close() }
  }

  fun openDrawer() {
    scope.launch { drawerState.open() }
  }

  ModalNavigationDrawer(
    drawerContent = {
      ModalDrawerSheet {
        Spacer(Modifier.height(12.dp))
        Area.entries.forEach {
          NavigationDrawerItem(
            icon = { Icon(Icons.Rounded.PlayArrow, contentDescription = null) },
            label = { Text(it.title) },
            selected = it.title == viewModel.title,
            onClick = {
              closeDrawer()
              if (it.title != viewModel.title) {
                scope.launch {
                  scrollState.scrollToItem(index = 0)
                  viewModel.selectedArea(it) {
                    listItems.refresh()
                  }
                }
              }
            },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
          )
        }
      }
    },
    drawerState = drawerState
  ) {
    Scaffold(topBar = {
      TopAppBar(
        title = { Text(text = viewModel.title) },
        modifier = Modifier.shadow(4.dp),
        actions = {
          IconButton(onClick = {
            listItems.refresh()
            scope.launch {
              scrollState.scrollToItem(0)
            }
          }) {
            Icon(imageVector = Icons.Rounded.Refresh, contentDescription = "refresh")
          }
        },
        navigationIcon = {
          IconButton(onClick = { openDrawer() }) {
            Icon(Icons.Rounded.Menu, contentDescription = "home icon")
          }
        })
    }) { paddingValues ->
      val refreshState = listItems.loadState.refresh
      when {
        refreshState is LoadState.Loading -> LoadingView(modifier = Modifier.padding(paddingValues))
        refreshState is LoadState.Error -> ErrorView(
          throwable = refreshState.error,
          onRetry = { listItems.retry() },
          modifier = Modifier.padding(paddingValues),
        )
        listItems.itemCount == 0 && listItems.loadState.append.endOfPaginationReached -> NoResultView(modifier = Modifier.padding(paddingValues))
        else -> {
        LazyColumn(state = scrollState, modifier = Modifier.padding(paddingValues)) {
          items(
            count = listItems.itemCount,
            key = listItems.itemKey { it.id },
          ) { index ->
            val item = listItems[index] ?: return@items
            Column(modifier = Modifier
              .fillMaxWidth()
              .clickable { openUrl(context, item.url) }
              .padding(10.dp)) {
              Text(
                buildAnnotatedString {
                  withStyle(style = SpanStyle(color = RowTitleColor, fontSize = 16.sp)) { append("${item.difficulty} ") }
                  withStyle(style = SpanStyle(color = RowTitleColor, fontSize = 20.sp)) { append(item.className) }
                }
              )
              RowView("신청기간", item.receivePeriod)
              RowView("교육기간", "${item.educatePeriod} ${item.educateDays}")
              RowView("잔여", item.remainNumber)
              RowView("수강료", item.fee)
              RowView("접수", item.howToRegister)
            }
            HorizontalDivider()
          }
        }
        }
      }
    }
  }
}

private fun openUrl(context: Context, url: String) {
  if (url.isBlank()) {
    Toast.makeText(context, R.string.error_no_url, Toast.LENGTH_SHORT).show()
    return
  }
  runCatching {
    context.startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
  }.onFailure {
    Toast.makeText(context, R.string.error_open_url, Toast.LENGTH_SHORT).show()
  }
}

@Composable
fun NoResultView(modifier: Modifier = Modifier) {
  Column(
    verticalArrangement = Arrangement.Center,
    modifier = modifier.fillMaxSize()
  ) {
    Text(
      text = stringResource(id = R.string.no_result),
      textAlign = TextAlign.Center,
      fontSize = 20.sp,
      modifier = Modifier.fillMaxWidth()
    )
  }
}

@Composable
fun LoadingView(modifier: Modifier = Modifier) {
  Box(
    contentAlignment = Alignment.Center,
    modifier = modifier.fillMaxSize()
  ) {
    CircularProgressIndicator()
  }
}

@Composable
fun ErrorView(throwable: Throwable, onRetry: () -> Unit, modifier: Modifier = Modifier) {
  val message = when (throwable) {
    is ApiException -> "${throwable.message} (${throwable.code})"
    else -> throwable.localizedMessage ?: stringResource(id = R.string.error_unknown)
  }
  Column(
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = modifier
      .fillMaxSize()
      .padding(20.dp)
  ) {
    Text(
      text = stringResource(id = R.string.error_title),
      textAlign = TextAlign.Center,
      fontSize = 20.sp,
      fontWeight = FontWeight.Bold,
      modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(8.dp))
    Text(
      text = message,
      textAlign = TextAlign.Center,
      fontSize = 16.sp,
      modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(16.dp))
    Button(onClick = onRetry) {
      Text(text = stringResource(id = R.string.retry))
    }
  }
}

@Composable
fun RowView(title: String, value: String) {
  Row(modifier = Modifier.padding(top = 10.dp, start = 0.dp)) {
    RowTitle(text = title)
    RowValue(text = value)
  }
}

@Composable
fun RowTitle(text: String) {
  Text(
    text = "$text: ",
    textAlign = TextAlign.End,
    maxLines = 1,
    fontSize = 16.sp,
    fontWeight = FontWeight.Bold,
    modifier = Modifier.defaultMinSize(70.dp)
  )
}

@Composable
fun RowValue(text: String) {
  Text(
    text = text,
    fontSize = 16.sp,
    fontWeight = FontWeight.Normal
  )
}
