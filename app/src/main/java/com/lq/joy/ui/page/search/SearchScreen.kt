package com.lq.joy.ui.page.search

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.OutlinedTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.lq.joy.TAG
import com.lq.joy.utils.Keyboard
import com.lq.joy.utils.keyboardAsState
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchScreen() {

    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned {
                Log.d(TAG, "parent:${it.size}")
            }
            .clickable(//去掉点击水波纹
                interactionSource = MutableInteractionSource(),
                indication = null,
                onClick = {
                    //去掉当前焦点
                    focusManager.clearFocus()
                })
    ) {
        var searchContent by remember { mutableStateOf("") }
        var isSearching by remember {
            mutableStateOf(false)
        }

        val y = remember {
            Animatable(0f)
        }
        val scope = rememberCoroutineScope()
        val screenWidth = LocalConfiguration.current.screenWidthDp
        val width = screenWidth * 0.8

        var distance by remember {
            mutableStateOf(-1f)
        }

        with(LocalDensity.current) {
            OutlinedTextField(
                value = searchContent,
                onValueChange = { searchContent = it },
                modifier = Modifier
                    .align(Alignment.Center)
                    .width(width.dp)
                    .height(64.dp)
                    .offset(y = y.value.toDp())
                    .onFocusChanged {
                        isSearching = it.isFocused
                    }
                    .onGloballyPositioned { coordinates ->
                        if (distance == -1f) {
                            val positionInParent = coordinates.positionInParent()
                            Log.d(
                                TAG,
                                "search box position:$positionInParent height:${coordinates.size.height}"
                            )
                            distance = positionInParent.y - 10.dp.toPx()
                        }
                    },
                shape = RoundedCornerShape(18.dp),
                singleLine = true,
                keyboardActions = KeyboardActions(
                    onDone = {
                        Log.d(TAG, "onDone")
                    },
                    onGo = {
                        Log.d(TAG, "onGo")

                    },
                    onSearch = {
                        Log.d(TAG, "onSearch")

                    },
                    onSend = {
                        Log.d(TAG, "onSend")

                    }
                )
            )
        }

        LaunchedEffect(key1 = isSearching) {
            scope.launch {
                if (isSearching) {
                    if (y.value == 0f) {
                        y.animateTo(-distance, animationSpec = tween(300))
                    }
                } else {
                    if (y.value == -distance) {
                        y.animateTo(0f, animationSpec = tween(300))
                    }

                }
            }
        }

        val keyboardAsState = keyboardAsState()
        LaunchedEffect(key1 = keyboardAsState.value) {
            scope.launch {
                when (keyboardAsState.value) {
                    Keyboard.Closed -> {
                        if (isSearching) focusManager.clearFocus()
                    }
                }
            }
        }


    }


}