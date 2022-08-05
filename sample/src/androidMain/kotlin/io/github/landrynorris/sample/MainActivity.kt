package io.github.landrynorris.sample

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.arkivanov.decompose.defaultComponentContext
import io.github.landrynorris.sample.components.SampleComponent
import io.github.landrynorris.sample.ui.SampleUI
import io.github.landrynorris.wirelesscommunication.NearbyConnectionNetworkDriver
import io.github.landrynorris.wirelesscommunication.SessionInfo
import java.util.UUID
import kotlin.random.Random

class MainActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sessionInfo = SessionInfo("my custom session", UUID.randomUUID().toString(),
            "A device with id ${Random.nextInt()}")
        val networkManager = NearbyConnectionNetworkDriver(this, sessionInfo)
        val logic = SampleComponent(defaultComponentContext(), networkManager)

        setContent {
            SampleUI(logic)
        }
    }
}