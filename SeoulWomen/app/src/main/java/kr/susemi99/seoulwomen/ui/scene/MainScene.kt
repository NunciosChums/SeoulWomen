package kr.susemi99.seoulwomen.ui.scene

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import kotlinx.coroutines.launch
import kr.susemi99.seoulwomen.R
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
        Area.values().forEach {
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
      if (listItems.itemCount == 0 && listItems.loadState.prepend.endOfPaginationReached) {
        NoResultView()
      } else {
        LazyColumn(state = scrollState, modifier = Modifier.padding(paddingValues)) {
          items(
            count = listItems.itemCount,
            key = listItems.itemKey { it.id },
          ) { index ->
            val it = listItems[index]
            Column(modifier = Modifier
              .fillMaxWidth()
              .clickable { Intent(Intent.ACTION_VIEW, Uri.parse(it?.url)).also { context.startActivity(it) } }
              .padding(10.dp)) {
              Text(
                buildAnnotatedString {
                  withStyle(style = SpanStyle(color = RowTitleColor, fontSize = 16.sp)) { append("${it?.difficulty} ") }
                  withStyle(style = SpanStyle(color = RowTitleColor, fontSize = 20.sp)) { append("${it?.className}") }
                }
              )
              RowView("신청기간", "${it?.receivePeriod}")
              RowView("교육기간", "${it?.educatePeriod} ${it?.educateDays}")
              RowView("잔여", "${it?.remainNumber}")
              RowView("수강료", "${it?.fee}")
              RowView("접수", "${it?.howToRegister}")
            }
            Divider()
          }
        }
      }
    }
  }
}

@Composable
fun NoResultView() {
  Column(
    verticalArrangement = Arrangement.Center,
    modifier = Modifier.fillMaxSize()
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

@Composable
fun DrawerMenuList(onSelected: (Area) -> Unit) {
  Column(
    modifier = Modifier
      .width(300.dp)
      .fillMaxHeight()
      .background(Color.DarkGray)
  ) {
    Area.values().forEach {
      TextButton(modifier = Modifier.fillMaxWidth(), shape = RectangleShape, onClick = { onSelected(it) }) {
        Text(text = it.title, fontSize = 20.sp)
      }
      Divider()
    }
  }
}