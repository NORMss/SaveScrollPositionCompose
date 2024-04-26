package com.norm.mysavescrollpositioncompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.norm.mysavescrollpositioncompose.ui.theme.MySaveScrollPositionComposeTheme
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce

@OptIn(FlowPreview::class)
class MainActivity : ComponentActivity() {
    private val prefs by lazy {
        applicationContext.getSharedPreferences("prefs", MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val scrollPosition = prefs.getInt("scroll_position", 0)
            MySaveScrollPositionComposeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val lazyListState = rememberLazyListState(
                        initialFirstVisibleItemIndex = scrollPosition
                    )

                    LaunchedEffect(key1 = lazyListState) {
                        snapshotFlow {
                            lazyListState.firstVisibleItemIndex
                        }
                            .debounce(500L)
                            .collectLatest { index ->
                                prefs.edit()
                                    .putInt("scroll_position", index)
                                    .apply()
                            }
                    }

                    LazyColumn(
                        state = lazyListState,
                        modifier = Modifier
                            .fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                    ) {
                        items(100) {
                            Text(
                                text = "Item $it",
                                modifier = Modifier
                                    .padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}