package com.nourtv.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class TvChannel(
    val id: String,
    val name: String,
    val category: String,
    val streamUrl: String,
    val enabled: Boolean = true
)

private val demoChannels = listOf(
    TvChannel("1", "قناة تجريبية مغربية", "المغرب", "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"),
    TvChannel("2", "قناة أخبار تجريبية", "أخبار", "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"),
    TvChannel("3", "قناة رياضية تجريبية", "رياضة", "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8")
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { NourTvApp() }
    }
}

@Composable
fun NourTvApp() {
    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("الكل") }
    val context = androidx.compose.ui.platform.LocalContext.current
    val prefs = remember { context.getSharedPreferences("nourtv_prefs", android.content.Context.MODE_PRIVATE) }
    var favorites by remember {
        mutableStateOf(prefs.getStringSet("favorites", emptySet())?.toSet() ?: emptySet())
    }
    var selectedChannel by remember { mutableStateOf<TvChannel?>(null) }
    val categories = listOf("الكل", "المغرب", "أخبار", "رياضة", "المفضلة")
    val visible = demoChannels.filter {
        it.enabled &&
        (selectedCategory == "الكل" ||
            (selectedCategory == "المفضلة" && it.id in favorites) ||
            it.category == selectedCategory) &&
        (it.name.contains(query, ignoreCase = true) || it.category.contains(query, ignoreCase = true))
    }

    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFFB68CFF),
            secondary = Color(0xFFFF426D),
            background = Color(0xFF0B0B12),
            surface = Color(0xFF171722)
        )
    ) {
        Surface(Modifier.fillMaxSize(), color = Color(0xFF0B0B12)) {
            if (selectedChannel == null) {
                Column(Modifier.fillMaxSize().padding(18.dp)) {
                    Text("NourTV", fontSize = 30.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Text("قنواتك المفضلة في مكان واحد", color = Color.LightGray, fontSize = 14.sp)
                    Spacer(Modifier.height(18.dp))
                    OutlinedTextField(
                        value = query, onValueChange = { query = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("ابحث عن قناة...") },
                        leadingIcon = { Icon(Icons.Default.Search, null) },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        categories.forEach { category ->
                            FilterChip(
                                selected = selectedCategory == category,
                                onClick = { selectedCategory = category },
                                label = { Text(category) }
                            )
                        }
                    }
                    Spacer(Modifier.height(18.dp))
                    Text("القنوات المباشرة", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(10.dp))
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(visible, key = { it.id }) { channel ->
                            Card(
                                modifier = Modifier.fillMaxWidth().clickable { selectedChannel = channel },
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF191925)),
                                shape = RoundedCornerShape(18.dp)
                            ) {
                                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        Modifier.size(48.dp).background(Color(0xFF302544), RoundedCornerShape(14.dp)),
                                        contentAlignment = Alignment.Center
                                    ) { Text("▶", color = Color(0xFFCFB2FF), fontSize = 20.sp) }
                                    Spacer(Modifier.width(12.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(channel.name, color = Color.White, fontWeight = FontWeight.Bold)
                                        Text(channel.category, color = Color.LightGray, fontSize = 12.sp)
                                    }
                                    IconButton(onClick = {
                                        favorites = if (channel.id in favorites) favorites - channel.id else favorites + channel.id
                                        prefs.edit().putStringSet("favorites", favorites).apply()
                                    }) {
                                        Icon(
                                            if (channel.id in favorites) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                            contentDescription = "المفضلة",
                                            tint = Color(0xFFFF5577)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Text("بيانات تجريبية — أضف روابط بث مرخّصة من لوحة الإدارة.", color = Color.Gray, fontSize = 11.sp)
                }
            } else {
                PlayerScreen(channel = selectedChannel!!, onBack = { selectedChannel = null })
            }
        }
    }
}
