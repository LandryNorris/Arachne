package io.github.landrynorris.sample.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import io.github.landrynorris.sample.components.DeviceListLogic

@Composable
fun SampleUI(logic: DeviceListLogic) {
    val state by logic.state.collectAsState()
    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn {
            items(state.devices) {
                Text("Device: ${it.name}@${it.address}")
            }
        }
    }

    LaunchedEffect(Unit) {
        logic.init()
    }
}
