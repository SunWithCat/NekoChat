@file:OptIn(ExperimentalMaterial3Api::class)

package com.sunwithcat.nekochat.ui.chat

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.sunwithcat.nekochat.R
import com.sunwithcat.nekochat.data.local.ApiKeyManager
import com.sunwithcat.nekochat.data.local.AvatarManager
import com.sunwithcat.nekochat.data.model.Author
import com.sunwithcat.nekochat.data.model.ChatMessage
import com.sunwithcat.nekochat.ui.AppSessionManager

@Composable
fun ChatScreen(
        conversationId: Long,
        onNavigateToSettings: (Long) -> Unit,
        onOpenDrawer: () -> Unit,
        onShowApiKeyDialog: () -> Unit
) {
    val context = LocalContext.current
    val factory = ChatViewModelFactory(context, conversationId)
    val viewModel: ChatViewModel =
            viewModel(
                    key = "chat_$conversationId", // 添加 key 确保正确的 ViewModel 实例
                    factory = factory
            )

    // 获取头像
    val avatarManager = remember(context) { AvatarManager(context) }
    val userAvatarUri = avatarManager.getUserAvatarUriString()
    val modelAvatarUri = avatarManager.getModelAvatarUriString()

    // 收集状态，状态改变时UI自动重建
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val messages by viewModel.messages.collectAsStateWithLifecycle()

    // 用于保存输入框内容的本地状态
    var userInput by remember { mutableStateOf("") }

    // 是否显示删除弹窗
    var showDeleteDialog by remember { mutableStateOf(false) }

    // 首次进入检查 API 密钥是否为空
    LaunchedEffect(conversationId) {
        if (conversationId == -1L) {
            val apiKeyManager = ApiKeyManager(context)
            if (apiKeyManager.getApiKey().isBlank() &&
                            !AppSessionManager.hasShownApiKeyPromptThisSession
            ) {
                onShowApiKeyDialog()
            }
        }
    }

    // 获取 LazyColumn 的滚动状态
    val lazyListState = rememberLazyListState()

    // 当消息列表更新时，滚动到最新消息
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            lazyListState.animateScrollToItem(messages.lastIndex)
        }
    }

    Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                        title = { Text("Neko Chat") },
                        colors =
                                TopAppBarDefaults.topAppBarColors(
                                        titleContentColor = MaterialTheme.colorScheme.primary,
                                        navigationIconContentColor =
                                                MaterialTheme.colorScheme.primary
                                ),
                        navigationIcon = {
                            IconButton(onClick = { onOpenDrawer() }) {
                                Icon(imageVector = Icons.Default.Menu, contentDescription = "菜单")
                            }
                        },
                        actions = {
                            IconButton(onClick = {onNavigateToSettings(conversationId)}) {
                                Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = "设置",
                                        tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            IconButton(
                                    onClick = { showDeleteDialog = true },
                                    enabled = messages.isNotEmpty()
                            ) {
                                Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "清空聊天记录",
                                        tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                )
            },
            bottomBar = {}
    ) { paddingValues ->
        if (showDeleteDialog) {
            AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("喵生震惊！") },
                    text = { Text("喂！不准删，笨蛋！删掉了...咬你哦！\n(｀Δ´)") },
                    confirmButton = {
                        TextButton(
                                onClick = {
                                    viewModel.clearChatHistory()
                                    showDeleteDialog = false
                                }
                        ) { Text("确定", color = MaterialTheme.colorScheme.error) }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) { Text("取消") }
                    }
            )
        }
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).imePadding()) {
            // 获取焦点管理器，用于清除焦点和文本选择
            val focusManager = LocalFocusManager.current

            Box(
                    modifier =
                            Modifier.weight(1f).pointerInput(Unit) {
                                detectTapGestures {
                                    // 点击空白区域时清除焦点，这会清除文本选择
                                    focusManager.clearFocus()
                                }
                            }
            ) {
                // 空状态提示
                if (messages.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                                text = "快来和本喵对话吧~\uD83D\uDC3E(ฅ>ω<*ฅ)",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } // 聊天消息列表
                else
                        LazyColumn(
                                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                                reverseLayout = false,
                                state = lazyListState // 将滚动状态传递给 LazyColumn
                        ) {
                            items(items = messages, key = { message -> message.id }) { message ->
                                ChatMessageItem(
                                        message = message,
                                        userAvatar = userAvatarUri,
                                        modelAvatar = modelAvatarUri,
                                        onRetry = { viewModel.retry() }
                                )
                            }
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
                        modifier = Modifier.padding(vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun ApiKeyInputDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var apiKey by remember { mutableStateOf(TextFieldValue("")) }
    val context = LocalContext.current
    val apiKeyManager = ApiKeyManager(context)
    val currentApiKey = apiKeyManager.getApiKey()

    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { apiKey = TextFieldValue(currentApiKey) }

    AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("设置 Gemini API 密钥") },
            text = {
                OutlinedTextField(
                        value = apiKey,
                        onValueChange = { apiKey = it },
                        label = { Text("Gemini API 密钥") },
                        singleLine = true,
                        visualTransformation =
                                if (passwordVisible) VisualTransformation.None
                                else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            val image =
                                    if (passwordVisible) Icons.Filled.Visibility
                                    else Icons.Filled.VisibilityOff

                            val description = if (passwordVisible) "隐藏密码" else "显示密码"
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = image, contentDescription = description)
                            }
                        }
                )
            },
            confirmButton = { TextButton(onClick = { onConfirm(apiKey.text) }) { Text("保存") } },
            dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } }
    )
}

@Composable
fun ChatMessageItem(
        message: ChatMessage,
        userAvatar: String?,
        modelAvatar: String?,
        onRetry: () -> Unit = {}
) {
    val isModel = message.author == Author.MODEL

    // 气泡形状：对方的消息左上角尖，自己的消息右上角尖
    val bubbleShape =
            if (isModel) {
                RoundedCornerShape(
                        topStart = 12.dp,
                        topEnd = 12.dp,
                        bottomEnd = 12.dp,
                        bottomStart = 4.dp
                )
            } else {
                RoundedCornerShape(
                        topStart = 12.dp,
                        topEnd = 12.dp,
                        bottomEnd = 4.dp,
                        bottomStart = 12.dp
                )
            }

    // 气泡颜色
    val backgroundColor =
            if (isModel) MaterialTheme.colorScheme.surfaceVariant
            else MaterialTheme.colorScheme.primary
    val contentColor =
            if (isModel) MaterialTheme.colorScheme.onSurfaceVariant
            else MaterialTheme.colorScheme.onPrimary

    Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), // 增加一点呼吸感
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = if (isModel) Arrangement.Start else Arrangement.End
    ) {
        // --- 左侧区域 ---
        if (isModel) {
            // AI 头像
            AsyncImage(
                    model = modelAvatar ?: R.drawable.ic_neko,
                    contentDescription = "AI Avatar",
                    modifier =
                            Modifier.size(40.dp)
                                    .clip(CircleShape)
                                    .border(
                                            1.dp,
                                            MaterialTheme.colorScheme.outlineVariant.copy(
                                                    alpha = 0.3f
                                            ),
                                            CircleShape
                                    ),
                    contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
        } else {
            Spacer(modifier = Modifier.width(48.dp))
        }

        // --- 中间气泡区域 ---
        Box(
                modifier = Modifier.weight(1f, fill = false) // 不占满剩余空间
        ) {
            Column(horizontalAlignment = if (isModel) Alignment.Start else Alignment.End) {
                // 消息气泡
                Surface(
                        shape = bubbleShape,
                        color = backgroundColor,
                        shadowElevation = 0.dp, // 轻微阴影增加立体感
                        modifier = Modifier.animateContentSize() // 内容变化时的平滑动画
                ) {
                    SelectionContainer {
                        Text(
                                text = message.content,
                                color = contentColor,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                        )
                    }
                }

                // 错误重试按钮 (显示在气泡下方)
                if (message.isError) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { onRetry() }
                    ) {
                        Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Retry",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                                text = "发送失败，点击重试",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }

        // --- 右侧区域 ---
        if (!isModel) {
            Spacer(modifier = Modifier.width(8.dp))
            // 用户头像
            AsyncImage(
                    model = userAvatar ?: R.drawable.ic_user_default,
                    contentDescription = "User Avatar",
                    modifier =
                            Modifier.size(40.dp)
                                    .clip(CircleShape)
                                    .border(
                                            1.dp,
                                            MaterialTheme.colorScheme.outlineVariant.copy(
                                                    alpha = 0.3f
                                            ),
                                            CircleShape
                                    ),
                    contentScale = ContentScale.Crop
            )
        } else {
            // AI消息：右侧留白，保持对称
            Spacer(modifier = Modifier.width(48.dp))
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
                            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 2.dp),
            verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                placeholder = @Composable { Text("与猫娘小苍进行对话") },
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
                modifier = Modifier.clip(CircleShape).background(MaterialTheme.colorScheme.primary)
        ) {
            Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Send",
                    tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}
