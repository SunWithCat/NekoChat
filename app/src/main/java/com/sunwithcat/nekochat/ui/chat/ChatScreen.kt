@file:OptIn(ExperimentalMaterial3Api::class)

package com.sunwithcat.nekochat.ui.chat

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sunwithcat.nekochat.data.local.ApiKeyManager
import com.sunwithcat.nekochat.data.model.Author
import com.sunwithcat.nekochat.data.model.ChatMessage
import com.sunwithcat.nekochat.ui.AppSessionManager
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(
    conversationId: Long,
    onNavigateToSettings: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToNewChat: () -> Unit
) {
    val context = LocalContext.current
    val factory = ChatViewModelFactory(context, conversationId)
    val viewModel: ChatViewModel = viewModel(factory = factory)

    // 收集状态，状态改变时UI自动重建
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val messages by viewModel.messages.collectAsStateWithLifecycle()

    // 用于保存输入框内容的本地状态
    var userInput by remember { mutableStateOf("") }

    // 是否显示删除弹窗
    var showDeleteDialog by remember { mutableStateOf(false) }

    // 是否显示 API key弹窗
    var showApikeyDialog by remember { mutableStateOf(false) }

    if (showApikeyDialog) {
        ApiKeyInputDialog(
            onDismiss = {
                showApikeyDialog = false
                AppSessionManager.hasShownApiKeyPromptThisSession = true
            },
            onConfirm = { apiKey ->
                val apiKeyManager = ApiKeyManager(context)
                apiKeyManager.saveApiKey(apiKey)
                Toast.makeText(context, "人家会好好记住这个秘密的喵~", Toast.LENGTH_SHORT).show()
                AppSessionManager.hasShownApiKeyPromptThisSession = true
                showApikeyDialog = false
            }
        )
    }

    // 首次进入检查 API 密钥是否为空
    LaunchedEffect(Unit) {
        if (conversationId == -1L) {
            val apiKeyManager = ApiKeyManager(context)
            if (apiKeyManager.getApiKey()
                    .isBlank() && !AppSessionManager.hasShownApiKeyPromptThisSession
            ) {
                showApikeyDialog = true
            }
        }
    }

    // 获取 LazyColumn 的滚动状态
    val lazyListState = rememberLazyListState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var selectedItem by remember { mutableStateOf("new_chat") }

    // 当消息列表更新时，滚动到最新消息
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            lazyListState.animateScrollToItem(messages.lastIndex)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor =
                    MaterialTheme.colorScheme.primaryContainer.copy(0.98f)
            ) {
                Column(modifier = Modifier.statusBarsPadding()) {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Neko Chat",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "你的专属猫娘助手",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                Modifier.padding(horizontal = 16.dp)
                HorizontalDivider(
                    thickness = 0.5.dp,
                    color =
                        MaterialTheme.colorScheme.onSurface.copy(
                            alpha = 0.1f
                        )
                )

                Spacer(modifier = Modifier.height(16.dp))

                NavigationDrawerItem(
                    icon = {
                        Icon(
                            Icons.Default.VpnKey,
                            contentDescription = "设置API Key"
                        )
                    },
                    label = { Text(text = "设置API Key") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        showApikeyDialog = true
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                NavigationDrawerItem(
                    icon = {
                        Icon(
                            Icons.Outlined.ChatBubbleOutline,
                            contentDescription = "新对话"
                        )
                    },
                    label = { Text(text = "开始新对话") },
                    selected = conversationId == -1L,
                    onClick = {
                        selectedItem = "new_chat"
                        scope.launch {
                            drawerState.close()
                        }
                        onNavigateToNewChat()
                    },
                    modifier = Modifier.padding(horizontal = 12.dp),
                    colors =
                        NavigationDrawerItemDefaults.colors(
                            unselectedContainerColor =
                                Color.Transparent,
                            selectedContainerColor =
                                MaterialTheme.colorScheme.primary
                                    .copy(alpha = 0.2f)
                        )
                )
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            Icons.Outlined.History,
                            contentDescription = "历史记录"
                        )
                    },
                    label = { Text(text = "历史记录") },
                    selected = selectedItem == "history",
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToHistory()
                    },
                    modifier = Modifier.padding(horizontal = 12.dp),
                    colors =
                        NavigationDrawerItemDefaults.colors(
                            unselectedContainerColor =
                                Color.Transparent,
                            selectedContainerColor =
                                MaterialTheme.colorScheme.primary
                                    .copy(alpha = 0.2f)
                        )
                )
                Spacer(modifier = Modifier.weight(1f))
                HorizontalDivider(
                    thickness = 0.5.dp,
                    color =
                        MaterialTheme.colorScheme.onSurface.copy(
                            alpha = 0.1f
                        )
                )
                Spacer(modifier = Modifier.height(16.dp))
                NavigationDrawerItem(
                    icon = {
                        Icon(Icons.Outlined.Info, contentDescription = "关于")
                    },
                    label = { Text(text = "关于 NekoChat") },
                    selected = selectedItem == "about",
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToAbout()
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Neko Chat") },
                    colors =
                        TopAppBarDefaults.topAppBarColors(
                            titleContentColor =
                                MaterialTheme.colorScheme.primary,
                            navigationIconContentColor =
                                MaterialTheme.colorScheme.primary
                        ),
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    drawerState.apply {
                                        if (isClosed) open()
                                        else close()
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "菜单"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = onNavigateToSettings) {
                            Icon(
                                imageVector =
                                    Icons.Default.Settings,
                                contentDescription = "设置",
                                tint =
                                    MaterialTheme.colorScheme
                                        .primary
                            )
                        }
                        IconButton(
                            onClick = { showDeleteDialog = true },
                            enabled = messages.isNotEmpty()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "清空聊天记录",
                                tint =
                                    MaterialTheme.colorScheme
                                        .primary
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
                        ) {
                            Text(
                                "确定",
                                color =
                                    MaterialTheme.colorScheme
                                        .error
                            )
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text("取消")
                        }
                    }
                )
            }
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .imePadding()
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    // 空状态提示
                    if (messages.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text =
                                    "快来和本喵对话吧~\uD83D\uDC3E(ฅ>ω<*ฅ)",
                                style =
                                    MaterialTheme.typography
                                        .titleLarge,
                                color =
                                    MaterialTheme.colorScheme
                                        .onSurfaceVariant
                            )
                        }
                    } // 聊天消息列表
                    else
                        LazyColumn(
                            modifier =
                                Modifier
                                    .fillMaxSize()
                                    .padding(
                                        horizontal = 16.dp
                                    ),
                            reverseLayout = false,
                            state = lazyListState // 将滚动状态传递给 LazyColumn
                        ) {
                            items(
                                items = messages,
                                key = { message -> message.id }
                            ) { message ->
                                ChatMessageItem(message = message)
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
                        isSendingEnabled =
                            !uiState.isModelProcessing // 模型处理时禁用发送按钮
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
                keyboardOptions =
                    KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image =
                        if (passwordVisible) Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff

                    val description = if (passwordVisible) "隐藏密码" else "显示密码"
                    IconButton(
                        onClick = { passwordVisible = !passwordVisible }
                    ) {
                        Icon(
                            imageVector = image,
                            contentDescription = description
                        )
                    }
                }
            )
        },
        confirmButton = { TextButton(onClick = { onConfirm(apiKey.text) }) { Text("保存") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } }
    )
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = alignment
    ) {
        Box(
            modifier =
                Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(backgroundColor)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            SelectionContainer {
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
            Modifier
                .fillMaxWidth()
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
            modifier =
                Modifier
                    .clip(CircleShape)
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
