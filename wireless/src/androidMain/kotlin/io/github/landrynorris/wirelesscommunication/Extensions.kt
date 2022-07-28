package io.github.landrynorris.wirelesscommunication

import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend fun <T> Task<T>.await() = suspendCoroutine<T> { continuation ->
    addOnSuccessListener { result ->
        continuation.resume(result)
    }
    addOnFailureListener {  e ->
        continuation.resumeWithException(e)
    }
}
