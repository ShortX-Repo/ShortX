@file:OptIn(ExperimentalFoundationApi::class)

package tornaco.apps.shortx.ui.base

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ShortXScrollableTabRow(
    selectedTabIndex: Int,
    modifier: Modifier = Modifier,
    containerColor: Color = TabRowDefaults.containerColor,
    contentColor: Color = TabRowDefaults.contentColor,
    edgePadding: Dp = 16.dp,
    indicator: @Composable (tabPositions: List<TabPosition>) -> Unit = @Composable { tabPositions ->
        TabRowDefaults.Indicator(
            Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex])
        )
    },
    divider: @Composable () -> Unit = @Composable {
        Divider()
    },
    tabs: @Composable () -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = selectedTabIndex,
        modifier = modifier,
        containerColor = containerColor,
        contentColor = contentColor,
        edgePadding = edgePadding,
        indicator = indicator,
        divider = divider,
        tabs = tabs
    )
}


@Composable
fun ShortXTabRow(
    selectedTabIndex: Int,
    onSelectedTab: (index: Int) -> Unit,
    items: List<String>,
) {
    AnimatedTab(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 16.dp)
            .height(48.dp),
        items = items,
        onSelectedTab = onSelectedTab,
        selectedTabIndex = selectedTabIndex
    )
}

@Composable
private fun AnimatedTab(
    items: List<String>,
    modifier: Modifier = Modifier,
    indicatorPadding: Dp = 0.dp,
    selectedTabIndex: Int = 0,
    onSelectedTab: (index: Int) -> Unit
) {
    var tabWidth by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    val indicatorOffset: Dp by animateDpAsState(
        if (selectedTabIndex == 0) {
            tabWidth * (selectedTabIndex / items.size.toFloat())
        } else {
            tabWidth * (selectedTabIndex / items.size.toFloat()) - indicatorPadding
        }, label = "indicatorOffset"
    )

    Box(
        modifier = modifier
            .onGloballyPositioned { coordinates ->
                tabWidth = with(density) {
                    coordinates.size.width.toDp()
                }
            }
            .background(
                color = ColorDefaults.backgroundSurfaceColor(0.5.dp),
                shape = RoundedCornerShape(50.dp)
            )
    ) {

        MyTabIndicator(
            modifier = Modifier
                .padding(indicatorPadding)
                .fillMaxHeight()
                .width(tabWidth / items.size - indicatorPadding),
            indicatorOffset = indicatorOffset
        )

        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            items.forEachIndexed { index, title ->
                MyTabItem(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(tabWidth / items.size),
                    onClick = {
                        onSelectedTab(index)
                    },
                    title = title
                )
            }
        }

    }
}

@Composable
private fun MyTabIndicator(
    modifier: Modifier,
    indicatorOffset: Dp,
) {
    Box(
        modifier = modifier
            .offset(x = indicatorOffset)
            .clip(RoundedCornerShape(50.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
    )
}

@Composable
private fun MyTabItem(
    modifier: Modifier,
    onClick: () -> Unit,
    title: String
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text = title, style = MaterialTheme.typography.titleSmall)
    }
}