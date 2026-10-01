package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.data.local.OrderEntity
import com.example.ui.screens.CartScreen
import com.example.ui.screens.CheckoutScreen
import com.example.ui.screens.MenuScreen
import com.example.ui.screens.OrderHistoryScreen
import com.example.ui.screens.OrderSuccessScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.CharcoalDark
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.FoodOrderViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

sealed class AppScreen {
    object Splash : AppScreen()
    object Menu : AppScreen()
    object Cart : AppScreen()
    object Checkout : AppScreen()
    data class OrderSuccess(val order: OrderEntity, val receiptText: String) : AppScreen()
    object History : AppScreen()
}

class MainActivity : ComponentActivity() {

    private val viewModel: FoodOrderViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                SalmanFoodApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun SalmanFoodApp(viewModel: FoodOrderViewModel) {
    val screenStack = remember { mutableStateListOf<AppScreen>(AppScreen.Splash) }
    val currentScreen = screenStack.lastOrNull() ?: AppScreen.Menu

    val canGoBack = screenStack.size > 1 && currentScreen !is AppScreen.Splash
    BackHandler(enabled = canGoBack) {
        if (screenStack.size > 1) {
            screenStack.removeAt(screenStack.lastIndex)
        }
    }

    val context = LocalContext.current
    var showUpdateDialog by remember { mutableStateOf(false) }
    var updateDownloadUrl by remember { mutableStateOf("") }
    var latestVersionName by remember { mutableStateOf("") }

    // बैकग्राउंड में नया अपडेट चेक करने का कोड
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                val apiUrl = URL("https://api.github.com/repos/qhistory498-coder/Salman-food/releases/latest")
                val connection = apiUrl.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("Accept", "application/vnd.github.v3+json")
                connection.connectTimeout = 5000
                connection.readTimeout = 5000

                if (connection.responseCode == 200) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    val json = JSONObject(response)
                    val tagName = json.optString("tag_name", "")
                    val assets = json.optJSONArray("assets")

                    var apkUrl = json.optString("html_url", "")
                    if (assets != null && assets.length() > 0) {
                        apkUrl = assets.getJSONObject(0).optString("browser_download_url", apkUrl)
                    }

                    val currentPackageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                    val currentVersion = currentPackageInfo.versionName ?: ""

                    if (tagName.isNotEmpty() && tagName != currentVersion && apkUrl.isNotEmpty()) {
                        latestVersionName = tagName
                        updateDownloadUrl = apkUrl
                        showUpdateDialog = true
                    }
                }
            } catch (_: Exception) {
                // इंटरनेट न होने पर सामान्य रूप से ऐप चलता रहेगा
            }
        }
    }

    // ग्राहक के सामने दिखने वाला अपडेट पॉप-अप बॉक्स
    if (showUpdateDialog) {
        AlertDialog(
            onDismissRequest = { showUpdateDialog = false },
            title = {
                Text(text = "🚀 नया अपडेट उपलब्ध है!", color = Color.White)
            },
            text = {
                Text(
                    text = "नया वर्शन ($latestVersionName) आ चुका है। नए फीचर्स, नए मेन्यू और बेहतर स्पीड के लिए अभी अपडेट करें।",
                    color = Color.LightGray
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showUpdateDialog = false
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(updateDownloadUrl))
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722))
                ) {
                    Text("अभी अपडेट करें", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showUpdateDialog = false }) {
                    Text("बाद में", color = Color.Gray)
                }
            },
            containerColor = Color(0xFF1E1E1E)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CharcoalDark)
            .navigationBarsPadding()
    ) {
        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "ScreenTransition"
        ) { screen ->
            when (screen) {
                is AppScreen.Splash -> {
                    SplashScreen(
                        onSplashFinished = {
                            screenStack.clear()
                            screenStack.add(AppScreen.Menu)
                        }
                    )
                }

                is AppScreen.Menu -> {
                    MenuScreen(
                        viewModel = viewModel,
                        onNavigateToCart = { screenStack.add(AppScreen.Cart) },
                        onNavigateToHistory = { screenStack.add(AppScreen.History) }
                    )
                }

                is AppScreen.Cart -> {
                    CartScreen(
                        viewModel = viewModel,
                        onNavigateBack = {
                            if (screenStack.size > 1) screenStack.removeAt(screenStack.lastIndex)
                        },
                        onProceedToCheckout = { screenStack.add(AppScreen.Checkout) }
                    )
                }

                is AppScreen.Checkout -> {
                    CheckoutScreen(
                        viewModel = viewModel,
                        onNavigateBack = {
                            if (screenStack.size > 1) screenStack.removeAt(screenStack.lastIndex)
                        },
                        onOrderSuccess = { order, receipt ->
                            screenStack.clear()
                            screenStack.add(AppScreen.Menu)
                            screenStack.add(AppScreen.OrderSuccess(order, receipt))
                        }
                    )
                }

                is AppScreen.OrderSuccess -> {
                    OrderSuccessScreen(
                        order = screen.order,
                        receiptText = screen.receiptText,
                        onBackToMenu = {
                            screenStack.clear()
                            screenStack.add(AppScreen.Menu)
                        },
                        onViewHistory = {
                            screenStack.clear()
                            screenStack.add(AppScreen.Menu)
                            screenStack.add(AppScreen.History)
                        }
                    )
                }

                is AppScreen.History -> {
                    OrderHistoryScreen(
                        viewModel = viewModel,
                        onNavigateBack = {
                            if (screenStack.size > 1) screenStack.removeAt(screenStack.lastIndex)
                        }
                    )
                }
            }
        }
    }
}

