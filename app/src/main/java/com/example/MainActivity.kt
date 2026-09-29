
package com.example

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.data.local.OrderEntity
import com.example.ui.screens.CartScreen
import com.example.ui.screens.CheckoutScreen
import com.example.ui.screens.MenuScreen
import com.example.ui.screens.OrdersHistoryScreen
import com.example.ui.screens.OrderSuccessScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.CharcoalDark
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.FoodOrderViewModel

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
    // Start with 4-second Animated 3D Splash Screen
    val screenStack = remember { mutableStateListOf<AppScreen>(AppScreen.Splash) }
    val currentScreen = screenStack.lastOrNull() ?: AppScreen.Menu

    val canGoBack = screenStack.size > 1 && currentScreen !is AppScreen.Splash
    BackHandler(enabled = canGoBack) {
        if (screenStack.size > 1) {
            screenStack.removeAt(screenStack.lastIndex)
        }
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
                    OrdersHistoryScreen(
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
