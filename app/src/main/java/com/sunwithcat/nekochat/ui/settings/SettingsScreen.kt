package com.sunwithcat.nekochat.ui.settings

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val factory = SettingsViewModelFactory(context)
    val viewModel: SettingsViewModel = viewModel(factory = factory)

    var promptText by remember { mutableStateOf(viewModel.getCurrentPrompt()) }

    var showRestoreDialog by remember { mutableStateOf(false) }

    if (showRestoreDialog) {
        AlertDialog(
                onDismissRequest = { showRestoreDialog = false },
                title = { Text("等等喵！") },
                text = { Text("确定要清除掉我们之间✨独特的约定✨，变回最初的设定喵？") },
                confirmButton = {
                    TextButton(
                            onClick = {
                                promptText = viewModel.getDefaultPrompt()
                                showRestoreDialog = false
                                Toast.makeText(context, "恢复成功！喵~", Toast.LENGTH_SHORT).show()
                            }
                    ) { Text("确定啦") }
                },
                dismissButton = {
                    TextButton(onClick = { showRestoreDialog = false }) { Text("我再想想") }
                }
        )
    }

    Scaffold(
            topBar = {
                TopAppBar(
                        title = { Text("角色设定") },
                        colors =
                                TopAppBarDefaults.topAppBarColors(
                                        titleContentColor = MaterialTheme.colorScheme.primary,
                                        navigationIconContentColor =
                                                MaterialTheme.colorScheme.primary,
                                        actionIconContentColor = MaterialTheme.colorScheme.primary
                                ),
                        navigationIcon = {
                            IconButton(onClick = onBack) {
                                Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        tint = MaterialTheme.colorScheme.primary,
                                        contentDescription = "返回"
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = { showRestoreDialog = true }) {
                                Icon(imageVector = Icons.Default.Restore, contentDescription = "恢复")
                            }
                            IconButton(
                                    onClick = {
                                        viewModel.savePrompt(promptText)
                                        Toast.makeText(context, "保存成功！喵~", Toast.LENGTH_SHORT)
                                                .show()
                                        onBack()
                                    }
                            ) { Icon(imageVector = Icons.Default.Done, contentDescription = "保存") }
                        }
                )
            }
    ) { paddingValues ->
        Column(
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp).imePadding()
        ) {
            OutlinedTextField(
                    value = promptText,
                    onValueChange = { promptText = it },
                    modifier = Modifier.fillMaxSize(),
                    label = { Text("在这里编辑你的专属猫娘设定...") }
            )
        }
    }
}
