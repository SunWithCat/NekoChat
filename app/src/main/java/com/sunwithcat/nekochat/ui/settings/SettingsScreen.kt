package com.sunwithcat.nekochat.ui.settings

import android.icu.text.DecimalFormat
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sunwithcat.nekochat.data.model.AIConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val factory = SettingsViewModelFactory(context)
    val viewModel: SettingsViewModel = viewModel(factory = factory)

    var promptText by remember { mutableStateOf(viewModel.getCurrentPrompt()) }

    var historyLength by remember { mutableStateOf(viewModel.getCurrentLength().toString()) }

    var temperature by remember { mutableFloatStateOf(viewModel.getCurrentTemperature()) }

    var tempFormatter = remember { DecimalFormat("0.0") }

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
                            if (historyLength.isNotBlank()) {
                                val lengthToSave =
                                    historyLength.toIntOrNull() ?: AIConfig.DEFAULT_CHAT_LENGTH
                                viewModel.saveLength(lengthToSave)
                            }
                            viewModel.saveTemperature(temperature)
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
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .imePadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = promptText,
                onValueChange = { promptText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                label = { Text("告诉我，我是一只怎样的猫娘？") },
                placeholder = {
                    Text(
                        "比如：你是一只白色短毛的异瞳猫娘，名字叫Neko，" +
                                "性格傲娇，喜欢吃鱼干，说话会带“喵”作为口癖，" +
                                "当别人夸你时会嘴硬但偷偷开心..."
                    )
                }
            )
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Text(
                    text = "调节人家的...“温度”？(${tempFormatter.format(temperature)})",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Slider(
                    value = temperature,
                    onValueChange = { temperature = it }, // 拖动时更新状态
                    valueRange = 0.0f..1.0f, // 温度范围
                    steps = 9
                )
                Text(
                    text = "温度越低，人家回答越稳定哦；温度越高，人家越...有创造力（也许会胡说八道喵？Σ(ﾟдﾟ;)）",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            OutlinedTextField(
                value = historyLength.toString(),
                onValueChange = { newText ->
                    historyLength = newText.filter { it.isDigit() }
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("主人，希望我记住多少对话呀？") },
                supportingText = { Text("数字越大，我记得越牢哦！但也会消耗更多能量喵...") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
        }
    }
}
