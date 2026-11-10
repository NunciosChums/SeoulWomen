package kr.susemi99.seoulwomen.ui.scene

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import kotlinx.coroutines.launch
import kr.susemi99.seoulwomen.R
import kr.susemi99.seoulwomen.api.ApiException
import kr.susemi99.seoulwomen.enums.Area
import kr.susemi99.seoulwomen.model.ReceiveStatus
import kr.susemi99.seoulwomen.model.RowItem
import java.io.IOException
import java.time.LocalDateTime

@Composable
fun MainScene() {
  val viewModel = hiltViewModel<MainViewModel>()
  val area by viewModel.area.collectAsState()
  MainContent(
    area = area,
    listItems = viewModel.list.collectAsLazyPagingItems(),
    onAreaSelected = viewModel::selectArea,
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(
  area: Area,
  listItems: LazyPagingItems<RowItem>,
  onAreaSelected: (Area) -> Unit,
  clock: () -> LocalDateTime = { LocalDateTime.now() },
) {
  val scrollState = rememberLazyListState()
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
        // 항목 전체 높이가 화면보다 큰 가로 모드에서도 모든 센터를 고를 수 있게 한다.
        Column(Modifier.verticalScroll(rememberScrollState())) {
          Spacer(Modifier.height(12.dp))
          Area.entries.forEach {
            NavigationDrawerItem(
              icon = { Icon(Icons.Rounded.PlayArrow, contentDescription = null) },
              label = { Text(it.title) },
              selected = it == area,
              onClick = {
                closeDrawer()
                if (it != area) {
                  // 에러/빈 화면에서는 LazyColumn이 컴포지션에 없어 scrollToItem이 끝나지 않으므로
                  // 지역 변경을 먼저 동기로 처리하고 스크롤은 별도 코루틴에서 수행한다.
                  onAreaSelected(it)
                  scope.launch { scrollState.scrollToItem(index = 0) }
                }
              },
              modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
          }
        }
      }
    },
    drawerState = drawerState
  ) {
    // 상태바·내비게이션바뿐 아니라 디스플레이 컷아웃(가로 모드 측면 노치)까지 피하도록 safeDrawing 사용
    Scaffold(
      contentWindowInsets = WindowInsets.safeDrawing,
      topBar = {
      TopAppBar(
        title = { Text(text = area.title) },
        modifier = Modifier.shadow(4.dp),
        windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top),
        actions = {
          IconButton(onClick = {
            listItems.refresh()
            scope.launch {
              scrollState.scrollToItem(0)
            }
          }) {
            Icon(imageVector = Icons.Rounded.Refresh, contentDescription = stringResource(id = R.string.content_description_refresh))
          }
        },
        navigationIcon = {
          IconButton(onClick = { openDrawer() }) {
            Icon(Icons.Rounded.Menu, contentDescription = stringResource(id = R.string.content_description_open_menu))
          }
        })
    }) { paddingValues ->
      val refreshState = listItems.loadState.refresh
      // 접수 상태의 기준 시각. 새로고침할 때마다 다시 읽어 오래 켜 둔 화면에서도 상태가 어긋나지 않게 한다.
      val now = remember(refreshState) { clock() }
      when {
        refreshState is LoadState.Loading -> LoadingView(modifier = Modifier.padding(paddingValues))
        refreshState is LoadState.Error -> ErrorView(
          throwable = refreshState.error,
          onRetry = { listItems.retry() },
          modifier = Modifier.padding(paddingValues),
        )
        listItems.itemCount == 0 && listItems.loadState.append.endOfPaginationReached -> NoResultView(modifier = Modifier.padding(paddingValues))
        else -> {
        // contentPadding으로 넘겨 리스트가 내비게이션바 뒤까지 스크롤되되 마지막 항목은 가려지지 않게 한다.
        LazyColumn(state = scrollState, contentPadding = paddingValues) {
          items(
            count = listItems.itemCount,
            key = listItems.itemKey { it.key },
          ) { index ->
            val item = listItems[index] ?: return@items
            ClassRow(item = item, now = now, onClick = { openUrl(context, item.url) })
            HorizontalDivider()
          }
          when (listItems.loadState.append) {
            is LoadState.Loading -> item(key = "append_loading") { AppendLoadingView() }
            is LoadState.Error -> item(key = "append_error") { AppendErrorView(onRetry = { listItems.retry() }) }
            is LoadState.NotLoading -> Unit
          }
        }
        }
      }
    }
  }
}

@Composable
fun ClassRow(item: RowItem, now: LocalDateTime, onClick: () -> Unit) {
  val today = now.toLocalDate()
  val status = item.receiveStatus(now)
  // 더 신청할 수 없는 강좌는 흐리게 표시하되, 내용을 확인할 수 있도록 누를 수는 있게 둔다.
  val inactive = status == ReceiveStatus.Closed || item.isFull
  val neutral = TagColors(MaterialTheme.colorScheme.surfaceContainerHighest, MaterialTheme.colorScheme.onSurfaceVariant)

  Column(
    verticalArrangement = Arrangement.spacedBy(6.dp),
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClickLabel = stringResource(id = R.string.action_open_web), onClick = onClick)
      .alpha(if (inactive) INACTIVE_ALPHA else 1f)
      .padding(horizontal = 16.dp, vertical = 14.dp)
  ) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
      Tag(text = statusText(status), colors = if (inactive) neutral else statusColors(status))
      if (item.difficultyName.isNotBlank()) {
        Tag(text = item.difficultyName, colors = neutral)
      }
      Spacer(Modifier.weight(1f))
      Icon(
        painter = painterResource(id = R.drawable.ic_open_in_new),
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.size(18.dp)
      )
    }
    Text(
      text = item.className,
      style = MaterialTheme.typography.titleLarge,
      fontWeight = FontWeight.SemiBold,
      modifier = Modifier.padding(top = 2.dp)
    )
    InfoRow(label = stringResource(id = R.string.label_receive_period), value = item.receivePeriod(today))
    InfoRow(label = stringResource(id = R.string.label_educate_period), value = item.educateSchedule(today))
    // 글꼴을 키워 한 줄에 다 들어가지 않으면 칩 묶음이 다음 줄로 내려간다.
    FlowRow(
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalArrangement = Arrangement.spacedBy(6.dp),
      itemVerticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 4.dp)
    ) {
      RemainText(item = item, inactive = inactive)
      Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        val freeColors = TagColors(MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.colorScheme.onTertiaryContainer)
        Tag(text = item.fee, colors = if (item.isFree && !inactive) freeColors else neutral)
        item.registerMethods.forEach { Tag(text = it, colors = neutral) }
      }
    }
    EnrollBar(
      ratio = item.enrolledRatio,
      color = when {
        inactive -> MaterialTheme.colorScheme.outline
        item.isAlmostFull -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.primary
      }
    )
  }
}

private const val INACTIVE_ALPHA = 0.6f

/** 마감까지 이 날 수 이하로 남으면 "접수 중" 대신 남은 날 수를 보여 준다. */
private const val CLOSING_SOON_DAYS = 7

/** 마감까지 이 날 수 이하로 남으면 배지를 경고색으로 바꾼다. */
private const val CLOSING_URGENT_DAYS = 3

private data class TagColors(val container: Color, val content: Color)

@Composable
private fun statusText(status: ReceiveStatus) = when (status) {
  ReceiveStatus.Upcoming -> stringResource(id = R.string.status_upcoming)
  ReceiveStatus.Closed -> stringResource(id = R.string.status_closed)
  is ReceiveStatus.Open -> when {
    status.daysLeft <= 0 -> stringResource(id = R.string.status_closing_today)
    status.daysLeft <= CLOSING_SOON_DAYS -> stringResource(id = R.string.status_closing_days, status.daysLeft)
    else -> stringResource(id = R.string.status_open)
  }
}

@Composable
private fun statusColors(status: ReceiveStatus) = when {
  status is ReceiveStatus.Open && status.daysLeft <= CLOSING_URGENT_DAYS ->
    TagColors(MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.onErrorContainer)
  status is ReceiveStatus.Open -> TagColors(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer)
  else -> TagColors(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.onSecondaryContainer)
}

@Composable
private fun Tag(text: String, colors: TagColors) {
  Text(
    text = text,
    style = MaterialTheme.typography.labelLarge,
    color = colors.content,
    maxLines = 1,
    modifier = Modifier
      .background(color = colors.container, shape = RoundedCornerShape(6.dp))
      .padding(horizontal = 8.dp, vertical = 3.dp)
  )
}

@Composable
private fun InfoRow(label: String, value: String) {
  Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
    Text(
      text = label,
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.alignByBaseline()
    )
    Text(
      text = value,
      style = MaterialTheme.typography.bodyLarge,
      modifier = Modifier.alignByBaseline()
    )
  }
}

@Composable
private fun RemainText(item: RowItem, inactive: Boolean) {
  val remain = if (item.isFull) {
    stringResource(id = R.string.remain_full)
  } else {
    stringResource(id = R.string.remain_seats, item.spareNum)
  }
  val total = stringResource(id = R.string.remain_total, item.collectNum)
  // 이미 신청할 수 없는 강좌에서는 자리가 얼마 안 남았다는 경고가 의미 없으므로 강조하지 않는다.
  val remainColor = if (item.isAlmostFull && !inactive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
  val totalColor = MaterialTheme.colorScheme.onSurfaceVariant
  Text(
    text = buildAnnotatedString {
      withStyle(style = SpanStyle(color = remainColor, fontWeight = FontWeight.Medium)) { append(remain) }
      withStyle(style = SpanStyle(color = totalColor)) { append(total) }
    },
    style = MaterialTheme.typography.bodyLarge
  )
}

/** 정원 대비 신청 인원을 보여 주는 막대. 같은 내용을 [RemainText]가 글로 전하므로 스크린리더에는 노출하지 않는다. */
@Composable
private fun EnrollBar(ratio: Float, color: Color) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(4.dp)
      .clip(RoundedCornerShape(2.dp))
      .background(MaterialTheme.colorScheme.surfaceContainerHighest)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth(ratio)
        .fillMaxHeight()
        .background(color)
    )
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
fun AppendLoadingView() {
  Box(
    contentAlignment = Alignment.Center,
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp)
  ) {
    CircularProgressIndicator()
  }
}

@Composable
fun AppendErrorView(onRetry: () -> Unit) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp)
  ) {
    Text(
      text = stringResource(id = R.string.error_title),
      textAlign = TextAlign.Center,
      fontSize = 16.sp,
      modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(8.dp))
    Button(onClick = onRetry) {
      Text(text = stringResource(id = R.string.retry))
    }
  }
}

@Composable
fun ErrorView(throwable: Throwable, onRetry: () -> Unit, modifier: Modifier = Modifier) {
  val message = when (throwable) {
    is ApiException -> "${throwable.message} (${throwable.code})"
    is IOException -> stringResource(id = R.string.error_network)
    else -> stringResource(id = R.string.error_unknown)
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
