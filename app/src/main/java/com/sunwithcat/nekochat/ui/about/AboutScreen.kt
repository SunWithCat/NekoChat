package com.sunwithcat.nekochat.ui.about

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sunwithcat.nekochat.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen (
    onBack: () -> Unit
) {
    val context = LocalContext.current
    Scaffold(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                title = { Text("关于 NekoChat") },
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_neko),
                contentDescription = "App Icon",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("NekoChat", style = MaterialTheme.typography.headlineSmall)
                Text("Version 1.0.0", style = MaterialTheme.typography.bodyMedium)
                Text("Ciallo～(∠・ω< )⌒★", style = MaterialTheme.typography.bodySmall)
                Text("领养你的猫娘“小苍”，开启一段暖心对话。", style = MaterialTheme.typography.bodyMedium)
            }

            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                modifier = Modifier.fillMaxWidth()
            ) {
                val uriHandler = LocalUriHandler.current
                val githubUrl = "https://github.com/SunWithCat/NekoChat"
                val homeUrl = "https://github.com/SunWithCat"
                Column(
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    ListItem(
                        headlineContent = {
                            Text("关于小苍", fontWeight = FontWeight.Bold)
                        },
                        supportingContent = {Text("一只软萌可爱、带点傲娇的猫娘~")},
                        leadingContent = {
                            Icon(Icons.Outlined.Pets, contentDescription = "简介")
                        },
                        colors = ListItemDefaults.colors(
                            containerColor = Color.Transparent
                        ),
                        modifier = Modifier.clickable{
                            Toast.makeText(
                                context,
                                "偷偷告诉你，在设置里可以改变人家的性格喔！(ฅ>ω<*ฅ)",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    )
                    HorizontalDivider()
                    ListItem(
                        headlineContent = {
                            Text("作者", fontWeight = FontWeight.Bold)
                        },
                        supportingContent = {Text("SunWithCat")},
                        leadingContent = {
                            Icon(Icons.Filled.Person, contentDescription = "作者")
                        },
                        colors = ListItemDefaults.colors(
                            containerColor = Color.Transparent
                        ),
                        modifier = Modifier.clickable {
                            uriHandler.openUri(homeUrl)
                        }
                    )
                    HorizontalDivider()
                    ListItem(
                        headlineContent = {
                            Text("开源地址", fontWeight = FontWeight.Bold)
                        },
                        supportingContent = {
                            Text(githubUrl)
                        },
                        leadingContent = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_github_octocat),
                                contentDescription = "GitHub",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        colors = ListItemDefaults.colors(
                            containerColor = Color.Transparent
                        ),
                        modifier = Modifier.clickable {
                            uriHandler.openUri(githubUrl)
                        }
                    )
                }
            }

        }
    }
}