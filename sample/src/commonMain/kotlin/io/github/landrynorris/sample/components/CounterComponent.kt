package io.github.landrynorris.sample.components

import com.arkivanov.decompose.ComponentContext
import io.github.landrynorris.sample.models.CounterModel
import io.github.landrynorris.wirelesscommunication.ArachneSession
import io.github.landrynorris.wirelesscommunication.Endpoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface CounterLogic {
    val state: MutableStateFlow<CounterState>
}

class CounterComponent(context: ComponentContext, private val session: ArachneSession):
    ComponentContext by context, CounterLogic {
    override val state = MutableStateFlow(CounterState())

    init {
        CoroutineScope(Dispatchers.Default).launch {
            session.listen(CounterModel.serializer()).collectLatest { message ->
                state.update { it.copy(counter = message.count) }
            }
        }
    }

    fun initialize() {
        CoroutineScope(Dispatchers.Default).launch {
            session.initialize()
        }
    }

    fun increment() {
    }

}

data class CounterState(val counter: Int = 0)
