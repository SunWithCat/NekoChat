@file:OptIn(ExperimentalMaterial3Api::class)

package com.sunwithcat.nekochat.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState // 导入 rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sunwithcat.nekochat.data.model.Author
import com.sunwithcat.nekochat.data.model.ChatMessage
//import dev.jeziellago.compose.markdowntext.MarkdownText

@Composable
fun ChatScreen(viewModel: ChatViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
        // 收集状态，状态改变时UI自动重建
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        // 用于保存输入框内容的本地状态
        var userInput by remember { mutableStateOf("") }

        // 获取 LazyColumn 的滚动状态
        val lazyListState = rememberLazyListState()

        // 当消息列表更新时，滚动到最新消息
        LaunchedEffect(uiState.messages.size) {
                if (uiState.messages.isNotEmpty()) {
                        lazyListState.animateScrollToItem(0) // 因为 reverseLayout = true，最新消息在索引 0
                }
        }

        Scaffold(
                topBar = {
                        TopAppBar(
                                title = { Text("Neko Chat") },
                                colors =
                                        TopAppBarDefaults.topAppBarColors(
                                                containerColor = MaterialTheme.colorScheme.primary,
                                                titleContentColor =
                                                        MaterialTheme.colorScheme.onPrimary
                                        )
                        )
                },
                bottomBar = {} // 移除 bottomBar 的内容
        ) { paddingValues ->
                Column(modifier = Modifier.fillMaxSize().padding(paddingValues).imePadding()) {
                        Box(modifier = Modifier.weight(1f)) {
                                // 空状态提示
                                if (uiState.messages.isEmpty()) {
                                        Box(
                                                modifier = Modifier.fillMaxSize(),
                                                contentAlignment = Alignment.Center
                                        ) {
                                                Text(
                                                        text = "快来和本喵对话吧~\uD83D\uDC3E(ฅ>ω<*ฅ)",
                                                        style = MaterialTheme.typography.titleLarge,
                                                        color =
                                                                MaterialTheme.colorScheme
                                                                        .onSurfaceVariant
                                                )
                                        }
                                } // 聊天消息列表
                                else
                                        LazyColumn(
                                                modifier =
                                                        Modifier.fillMaxSize()
                                                                .padding(horizontal = 16.dp),
                                                reverseLayout = true,
                                                state = lazyListState // 将滚动状态传递给 LazyColumn
                                        ) {
                                                items(
                                                        items = uiState.messages.reversed(),
                                                        key = { message -> message.id }
                                                ) { message -> ChatMessageItem(message = message) }
                                        }
                        }

                        // 输入框和底部信息
                        Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                                MessageInput(
                                        value = userInput,
                                        onValueChange = { userInput = it },
                                        onSendClick = {
                                                viewModel.sendMessage(userInput)
                                                userInput = "" // 发送后清空输入框
                                        },
                                        isSendingEnabled = !uiState.isModelProcessing // 模型处理时禁用发送按钮
                                )
                                Text(
                                        text = "Based on Google Gemini-2.5-Flash",
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(vertical = 2.dp)
                                )
                                Text(
                                        text = "Developed by SunWithCat",
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                )
                        }
                }
        }
}

@Composable
fun ChatMessageItem(message: ChatMessage) {
        val isModel = message.author == Author.MODEL
        val alignment = if (isModel) Alignment.CenterStart else Alignment.CenterEnd
        val backgroundColor =
                if (isModel) MaterialTheme.colorScheme.surfaceVariant
                else MaterialTheme.colorScheme.primary
        val textColor =
                if (isModel) MaterialTheme.colorScheme.onSurfaceVariant
                else MaterialTheme.colorScheme.onPrimary

        // 用一个Box来控制对齐
        Box(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                contentAlignment = alignment
        ) {
                Box(
                        modifier =
                                Modifier.clip(RoundedCornerShape(12.dp))
                                        .background(backgroundColor)
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                        SelectionContainer {
//                                if (isModel) {
//                                        MarkdownText(
//                                                markdown = message.content,
//                                                color = textColor,
//                                                style =
//                                                        MaterialTheme.typography.bodyLarge.copy(
//                                                                color = textColor
//                                                        )
//                                        )
//                                } else {
//                                        Text(
//                                                text = message.content,
//                                                color = textColor,
//                                                style = MaterialTheme.typography.bodyLarge
//                                        )
//                                }
                                Text(
                                        text = message.content,
                                        color = textColor,
                                        style = MaterialTheme.typography.bodyLarge
                                )
                        }
                }
        }
}

@Composable
fun MessageInput(
        value: String,
        onValueChange: (String) -> Unit,
        onSendClick: () -> Unit,
        isSendingEnabled: Boolean
) {
        Row(
                modifier =
                        Modifier.fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
        ) {
                TextField(
                        value = value,
                        onValueChange = onValueChange,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        placeholder = @Composable { Text("Type a message...") },
                        colors =
                                TextFieldDefaults.colors(
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        disabledIndicatorColor = Color.Transparent
                                )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                        onClick = onSendClick,
                        enabled = isSendingEnabled,
                        modifier =
                                Modifier.clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary)
                ) {
                        Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Send",
                                tint = MaterialTheme.colorScheme.onPrimary
                        )
                }
        }
}
