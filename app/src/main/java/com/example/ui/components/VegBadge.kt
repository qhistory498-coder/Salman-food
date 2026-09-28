package com.example.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.data.model.DietType
import com.example.ui.theme.EggAmber
import com.example.ui.theme.NonVegRed
import com.example.ui.theme.VegGreen

@Composable
fun VegBadge(
    dietType: DietType,
    modifier: Modifier = Modifier,
    size: Int = 16
) {
    val (color, shapeDesc) = when (dietType) {
        DietType.VEG -> VegGreen to "Veg"
        DietType.NON_VEG -> NonVegRed to "Non-Veg"
        DietType.EGG -> EggAmber to "Egg"
    }

    Box(
        modifier = modifier
            .size(size.dp)
            .border(1.5.dp, color, RoundedCornerShape(3.dp)),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .size((size * 0.5f).dp)
                .clip(CircleShape),
            color = color
        ) {}
    }
}
