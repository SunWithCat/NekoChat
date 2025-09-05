package com.sunwithcat.nekochat.ui.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sunwithcat.nekochat.data.model.Conversation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onBack: () -> Unit,
    onConversationClick: (Long) -> Unit
){
    val context = LocalContext.current
    val factory = HistoryViewModelFactory(context)
    val viewModel: HistoryViewModel = viewModel(factory = factory)
    val conversations by viewModel.conversations.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {Text("历史记录")},
                colors = TopAppBarDefaults.topAppBarColors(
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues)
        ) {
            items(
                items = conversations,
                key = {it.id}
            ) { conversation ->
                ConversationItem(
                    conversation = conversation,
                    onClick = {onConversationClick(conversation.id)}
                )
            }
        }

    }
}

@Composable
fun ConversationItem(conversation: Conversation, onClick: () -> Unit) {
    ListItem(
        headlineContent = {Text(conversation.title, maxLines = 1)},
        supportingContent = {Text("${conversation.lastMessageTimestamp}")},
        modifier = Modifier.clickable(onClick = onClick)
    )
}