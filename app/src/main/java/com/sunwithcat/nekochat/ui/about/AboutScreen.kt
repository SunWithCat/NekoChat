package com.sunwithcat.nekochat.ui.about

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sunwithcat.nekochat.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBack: () -> Unit) {
    // 拦截系统返回手势，使用我们的 onBack
    BackHandler(onBack = onBack)
    val context = LocalContext.current

    val colors = MaterialTheme.colorScheme
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }

    Scaffold(
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = colors.surface,
                        titleContentColor = colors.primary,
                        actionIconContentColor = colors.primary,
                        navigationIconContentColor = colors.primary
                    ),
                title = { Text("关于 NekoChat") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn() + slideInVertically { it / 2 },
            modifier = Modifier.padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // 圆形 Logo 增加一点呼吸感
                Image(
                    painter = painterResource(id = R.drawable.ic_neko),
                    contentDescription = "App Icon",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        "NekoChat",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        "Version 1.0.0",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        "Ciallo～(∠・ω< )⌒★",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        "领养你的猫娘“小苍”，开启一段暖心对话。",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                OutlinedCard(
                    colors =
                        CardDefaults.outlinedCardColors(
                            containerColor =
                                MaterialTheme.colorScheme.surfaceVariant.copy(
                                    alpha = 0.3f
                                )
                        ),
                    shape = RoundedCornerShape(24.dp),
                    border =
                        BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val uriHandler = LocalUriHandler.current
                    val githubUrl = "https://github.com/SunWithCat/NekoChat"
                    val homeUrl = "https://github.com/SunWithCat"

                    Column {
                        ListItem(
                            headlineContent = {
                                Text("关于小苍", fontWeight = FontWeight.SemiBold)
                            },
                            supportingContent = { Text("一只软萌可爱、带点傲娇的猫娘~") },
                            leadingContent = {
                                Icon(
                                    Icons.Outlined.Pets,
                                    contentDescription = "简介",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            colors =
                                ListItemDefaults.colors(containerColor = Color.Transparent),
                            modifier =
                                Modifier.clickable {
                                    Toast.makeText(
                                        context,
                                        "偷偷告诉你，在设置里可以改变人家的性格喔！(ฅ>ω<*ฅ)",
                                        Toast.LENGTH_LONG
                                    )
                                        .show()
                                }
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        )
                        ListItem(
                            headlineContent = { Text("作者", fontWeight = FontWeight.SemiBold) },
                            supportingContent = { Text("SunWithCat") },
                            leadingContent = {
                                Icon(
                                    Icons.Filled.Person,
                                    contentDescription = "作者",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            colors =
                                ListItemDefaults.colors(containerColor = Color.Transparent),
                            modifier = Modifier.clickable { uriHandler.openUri(homeUrl) }
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        )
                        ListItem(
                            headlineContent = {
                                Text("开源地址", fontWeight = FontWeight.SemiBold)
                            },
                            supportingContent = { Text(githubUrl) },
                            leadingContent = {
                                Icon(
                                    painter =
                                        painterResource(
                                            id = R.drawable.ic_github_octocat
                                        ),
                                    contentDescription = "GitHub",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            colors =
                                ListItemDefaults.colors(containerColor = Color.Transparent),
                            modifier = Modifier.clickable { uriHandler.openUri(githubUrl) }
                        )
                    }
                }
            }
        }
    }
}
