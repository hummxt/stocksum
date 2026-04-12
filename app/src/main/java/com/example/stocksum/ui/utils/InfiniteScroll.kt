package com.example.stocksum.ui.utils

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow

/**
 * Detects when user scrolls to the bottom of a LazyColumn and triggers load more callback.
 * Simple and reliable approach using visual item detection.
 *
 * Usage:
 * val lazyListState = rememberLazyListState()
 * LazyColumn(state = lazyListState) {
 *     // ... your items
 * }
 * LazyListenedEnd(lazyListState) { viewModel.loadMoreStocks() }
 */
@Composable
fun LazyListenedEnd(
    lazyListState: LazyListState,
    onLoadMore: () -> Unit,
    threshold: Int = 3
) {
    val onLoadMoreState = rememberUpdatedState(onLoadMore)
    
    LaunchedEffect(lazyListState) {
        snapshotFlow {
            if (lazyListState.layoutInfo.visibleItemsInfo.isEmpty()) {
                return@snapshotFlow -1
            }
            
            // Get the index of the last visible item
            lazyListState.layoutInfo.visibleItemsInfo.last().index
        }.collect { lastVisibleIndex ->
            val totalItems = lazyListState.layoutInfo.totalItemsCount
            
            // If we can see items and are near the end, load more
            if (lastVisibleIndex >= totalItems - threshold && totalItems > 0) {
                onLoadMoreState.value()
            }
        }
    }
}

/**
 * Alias for backwards compatibility
 */
@Composable
fun InfiniteScrollHandler(
    lazyListState: LazyListState,
    itemCount: Int = 0,
    onLoadMore: () -> Unit,
    threshold: Int = 3
) {
    LazyListenedEnd(
        lazyListState = lazyListState,
        onLoadMore = onLoadMore,
        threshold = threshold
    )
}
