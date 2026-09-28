package com.example.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FoodCategory
import com.example.ui.theme.CharcoalCard
import com.example.ui.theme.CharcoalSurfaceVariant
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.GoldenYellow
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VegGreen

@Composable
fun CategoryChips(
    selectedCategory: FoodCategory,
    vegOnly: Boolean,
    onCategorySelected: (FoodCategory) -> Unit,
    onToggleVegOnly: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Pure Veg filter toggle chip
        FilterChip(
            selected = vegOnly,
            onClick = onToggleVegOnly,
            label = {
                Text(
                    text = "Veg Only",
                    fontWeight = if (vegOnly) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 12.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Spa,
                    contentDescription = "Veg Only",
                    tint = if (vegOnly) VegGreen else TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
            },
            shape = RoundedCornerShape(12.dp),
            colors = FilterChipDefaults.filterChipColors(
                containerColor = CharcoalCard,
                labelColor = TextSecondary,
                selectedContainerColor = VegGreen.copy(alpha = 0.2f),
                selectedLabelColor = VegGreen
            ),
            border = FilterChipDefaults.filterChipBorder(
                borderColor = if (vegOnly) VegGreen else CharcoalSurfaceVariant,
                selectedBorderColor = VegGreen,
                borderWidth = 1.dp,
                selectedBorderWidth = 1.5.dp,
                enabled = true,
                selected = vegOnly
            ),
            modifier = Modifier.testTag("filter_veg_only")
        )

        // Category filter chips
        FoodCategory.values().forEach { category ->
            val isSelected = category == selectedCategory
            FilterChip(
                selected = isSelected,
                onClick = { onCategorySelected(category) },
                label = {
                    Text(
                        text = category.displayName,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 12.sp
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = CharcoalCard,
                    labelColor = TextSecondary,
                    selectedContainerColor = FlameOrange,
                    selectedLabelColor = Color.White
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = CharcoalSurfaceVariant,
                    selectedBorderColor = GoldenYellow,
                    borderWidth = 1.dp,
                    selectedBorderWidth = 1.5.dp,
                    enabled = true,
                    selected = isSelected
                ),
                modifier = Modifier.testTag("category_chip_${category.name.lowercase()}")
            )
        }
    }
}
