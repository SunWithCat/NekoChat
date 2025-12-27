package com.sunwithcat.nekochat.ui.apisettings

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Key
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.sunwithcat.nekochat.data.local.ApiKeyManager
import com.sunwithcat.nekochat.data.local.ApiProvider
import com.sunwithcat.nekochat.ui.settings.SettingsSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiSettingsScreen(onBack: () -> Unit) {
    BackHandler { onBack() }

    val context = LocalContext.current
    val apiKeyManager = remember { ApiKeyManager(context) }

    var selectedProvider by remember { mutableStateOf(apiKeyManager.getProvider()) }
    var googleApiKey by remember { mutableStateOf(apiKeyManager.getApiKey()) }
    var openaiBaseUrl by remember { mutableStateOf(apiKeyManager.getOpenAIBaseUrl()) }
    var openaiApiKey by remember { mutableStateOf(apiKeyManager.getOpenAIApiKey()) }
    var openaiModel by remember { mutableStateOf(apiKeyManager.getOpenAIModel()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("API 设置") },
                colors = TopAppBarDefaults.topAppBarColors(
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary,
                    actionIconContentColor = MaterialTheme.colorScheme.primary
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            apiKeyManager.saveProvider(selectedProvider)
                            apiKeyManager.saveApiKey(googleApiKey)
                            apiKeyManager.saveOpenAIApiKey(openaiApiKey)
                            apiKeyManager.saveOpenAIBaseUrl(openaiBaseUrl)
                            apiKeyManager.saveOpenAIModel(openaiModel)
                            Toast.makeText(context, "保存成功喵~", Toast.LENGTH_SHORT).show()
                            onBack()
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Done, contentDescription = "保存")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            SettingsSection(
                title = "选择 API 提供商",
                icon = Icons.Default.Cloud,
                content = {
                    Column(Modifier.selectableGroup()) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = selectedProvider == ApiProvider.GOOGLE,
                                    onClick = { selectedProvider = ApiProvider.GOOGLE },
                                    role = Role.RadioButton
                                )
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedProvider == ApiProvider.GOOGLE,
                                onClick = null
                            )
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text("Google Gemini", style = MaterialTheme.typography.bodyLarge)
                                Text(
                                    "使用 Google 的 Gemini 模型",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Row(
                            Modifier.fillMaxWidth().selectable(
                                selected = selectedProvider == ApiProvider.OPENAI,
                                onClick = { selectedProvider = ApiProvider.OPENAI },
                                role = Role.RadioButton
                            )
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedProvider == ApiProvider.OPENAI,
                                onClick = null
                            )
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text("OpenAI 兼容", style = MaterialTheme.typography.bodyLarge)
                                Text(
                                    "支持 DeepSeek 等接口",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            )

            // Google
            if (selectedProvider == ApiProvider.GOOGLE) {
                SettingsSection(
                    title = "Google API 配置",
                    icon = Icons.Default.Key,
                    content = {
                        OutlinedTextField(
                            value = googleApiKey,
                            onValueChange = { googleApiKey = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = {Text("API Key") },
                            placeholder = {Text("输入你的 Google API Key")},
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true
                        )
                    }
                )
            }
            if (selectedProvider == ApiProvider.OPENAI) {
                SettingsSection(
                    title = "OpenAI 兼容 API 配置",
                    icon = Icons.Default.Key,
                    content = {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            OutlinedTextField(
                                value = openaiBaseUrl,
                                onValueChange = { openaiBaseUrl = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Base URL") },
                                placeholder = { Text("例如: https://api.deepseek.com/v1") },
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = openaiApiKey,
                                onValueChange = { openaiApiKey = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("API Key") },
                                placeholder = { Text("输入你的 API Key") },
                                visualTransformation = PasswordVisualTransformation(),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = openaiModel,
                                onValueChange = { openaiModel = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("模型名称") },
                                placeholder = { Text("例如: deepseek-chat") },
                                singleLine = true
                            )
                        }
                    }
                )
            }
        }
    }
}