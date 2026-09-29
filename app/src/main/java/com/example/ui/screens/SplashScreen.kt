
package com.example.ui.screens

import android.media.AudioManager
import android.media.ToneGenerator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CharcoalDark
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.GoldenYellow
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    // 3D Pulse, Scale & Rotation Animations
    val scale = remember { Animatable(0.2f) }
    val alpha = remember { Animatable(0f) }
    val rotation = remember { Animatable(-30f) }
    val ringRotation = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Soft System Welcome Sound Effect (Safe & No Extra Files Needed)
        try {
            val toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 75)
            toneGen.startTone(ToneGenerator.TONE_PROP_BEEP2, 180)
        } catch (_: Exception) { }

        // Start 3D Motion
        launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 1100, easing = FastOutSlowInEasing)
            )
        }
        launch {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 800)
            )
        }
        launch {
            rotation.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 1100, easing = FastOutSlowInEasing)
            )
        }
        launch {
            ringRotation.animateTo(
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(3500, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
        }

        // Wait 4 Seconds exactly as requested, then launch dashboard
        delay(4000L)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF221A15),
                        CharcoalDark,
                        Color(0xFF0F0E0E)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .scale(scale.value)
                .alpha(alpha.value)
                .rotate(rotation.value)
        ) {
            // Premium 3D Golden-Orange S Logo Badge
            Box(contentAlignment = Alignment.Center) {
                // Outer Glow / Ring
                Surface(
                    shape = RoundedCornerShape(36.dp),
                    color = Color.Transparent,
                    modifier = Modifier
                        .size(145.dp)
                        .rotate(ringRotation.value)
                        .border(
                            2.dp,
                            Brush.sweepGradient(
                                listOf(
                                    FlameOrange,
                                    GoldenYellow,
                                    Color.Transparent,
                                    FlameOrange
                                )
                            ),
                            RoundedCornerShape(36.dp)
                        )
                ) {}

                // Core 3D Icon Container
                Surface(
                    shape = RoundedCornerShape(32.dp),
                    color = Color(0xFF1B1917),
                    shadowElevation = 18.dp,
                    modifier = Modifier
                        .size(130.dp)
                        .border(1.5.dp, Color(0xFF4A3423), RoundedCornerShape(32.dp))
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Chef Flame Icon at Top Right
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = GoldenYellow.copy(alpha = 0.85f),
                            modifier = Modifier
                                .size(28.dp)
                                .align(Alignment.TopEnd)
                                .padding(top = 16.dp, end = 16.dp)
                        )

                        // 3D "S" Letter Emblem
                        Text(
                            text = "S",
                            fontSize = 72.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = FlameOrange,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            // App Brand Name (Zomato Style Bold Branding)
            Text(
                text = "SALMAN FOOD",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 3.sp,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "⚡ Hot, Fresh & Fast Delivery",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = GoldenYellow
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Small 3D Food Badge (Chowmein, Rolls & Fast Food)
            Surface(
                shape = CircleShape,
                color = Color(0xFF26201A),
                border = androidx.compose.foundation.BorderStroke(1.dp, FlameOrange.copy(alpha = 0.4f))
            ) {
                androidx.compose.foundation.layout.Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Restaurant,
                        contentDescription = null,
                        tint = FlameOrange,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(
                        text = "CHOWMEIN • ROLLS • BIRYANI",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}
