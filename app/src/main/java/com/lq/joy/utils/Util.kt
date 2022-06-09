package com.lq.joy.utils

import android.content.Context
import android.graphics.Rect
import android.view.ViewTreeObserver
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

enum class Keyboard {
    Opened, Closed
}

@Composable
fun keyboardAsState(): State<Keyboard> {
    val keyboardState = remember { mutableStateOf(Keyboard.Closed) }
    val view = LocalView.current
    DisposableEffect(view) {
        val onGlobalListener = ViewTreeObserver.OnGlobalLayoutListener {
            val rect = Rect()
            view.getWindowVisibleDisplayFrame(rect)
            val screenHeight = view.rootView.height
            val keypadHeight = screenHeight - rect.bottom
            keyboardState.value = if (keypadHeight > screenHeight * 0.15) {
                Keyboard.Opened
            } else {
                Keyboard.Closed
            }
        }
        view.viewTreeObserver.addOnGlobalLayoutListener(onGlobalListener)

        onDispose {
            view.viewTreeObserver.removeOnGlobalLayoutListener(onGlobalListener)
        }
    }

    return keyboardState
}

/**
 * ViewModel扩展方法：启动协程
 * @param block 协程逻辑
 * @param onError 错误回调方法
 * @param onComplete 完成回调方法
 */
fun ViewModel.launch(
    block: suspend CoroutineScope.() -> Unit,
    onError: (e: Throwable) -> Unit = { _: Throwable -> },
    onComplete: () -> Unit = {}
) {
    viewModelScope.launch(
        CoroutineExceptionHandler { _, throwable ->
            onError(throwable)
        }
    ) {
        try {
            block.invoke(this)
        } finally {
            onComplete()
        }
    }
}

fun getLocalString(context: Context, fileName: String): String? {
    var inputStream: InputStream? = null
    var reader: InputStreamReader? = null
    try {
        inputStream = context.assets.open(fileName)
        reader = InputStreamReader(inputStream)
        return reader.readText()
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        try {
            inputStream?.close()
            reader?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    return null
}

val LazyListState.isScrolled: Boolean
    get() = firstVisibleItemIndex > 0 || firstVisibleItemScrollOffset > 0