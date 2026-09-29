package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.MenuItem
import com.example.data.model.PortionOption
import com.example.ui.theme.CharcoalCard
import com.example.ui.theme.CharcoalDark
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.CharcoalSurfaceVariant
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.GoldenYellow
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun MenuItemCard(
    menuItem: MenuItem,
    getQuantity: (portionName: String) -> Int,
    onAddToCart: (portion: PortionOption) -> Unit,
    onRemoveFromCart: (portionName: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 7.dp)
            .testTag("menu_item_${menuItem.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = CharcoalCard
        ),
        border = BorderStroke(1.dp, CharcoalSurfaceVariant.copy(alpha = 0.8f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Main Top Section: Details on Left, Image & Add Button on Right (Zomato Pattern)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Left Column: Name, Description & Badges
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        VegBadge(
                            dietType = menuItem.dietType,
                            modifier = Modifier.padding(end = 6.dp)
                        )

                        if (menuItem.isBestseller) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = FlameOrange.copy(alpha = 0.18f),
                                border = BorderStroke(0.6.dp, FlameOrange)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = FlameOrange,
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "MUST TRY",
                                        color = FlameOrange,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        }

                        if (menuItem.isSpicy) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF3E1C15)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocalFireDepartment,
                                        contentDescription = "Spicy",
                                        tint = FlameOrange,
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = "Spicy",
                                        color = FlameOrange,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = menuItem.name,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )

                    if (menuItem.hindiName.isNotBlank()) {
                        Text(
                            text = menuItem.hindiName,
                            color = GoldenYellow,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    if (menuItem.description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = menuItem.description,
                            color = TextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            maxLines = 2
                        )
                    }
                }

                // Right: Big Food Image (Zomato Card Style)
                if (menuItem.imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = menuItem.imageUrl,
                        contentDescription = menuItem.name,
                        modifier = Modifier
                            .size(86.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.dp, CharcoalSurfaceVariant, RoundedCornerShape(14.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Portion Options & Counters (Full/Half/Regular)
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                menuItem.portions.forEach { portion ->
                    val qty = getQuantity(portion.name)
                    PortionRow(
                        portion = portion,
                        quantity = qty,
                        onAdd = { onAddToCart(portion) },
                        onRemove = { onRemoveFromCart(portion.name) },
                        itemId = menuItem.id
                    )
                }
            }
        }
    }
}

@Composable
private fun PortionRow(
    portion: PortionOption,
    quantity: Int,
    onAdd: () -> Unit,
    onRemove: () -> Unit,
    itemId: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = CharcoalDark.copy(alpha = 0.7f),
        border = BorderStroke(0.8.dp, CharcoalSurfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = portion.name,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "₹${portion.price}",
                    color = GoldenYellow,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp
                )
            }

            // Zomato Style ADD Button / Counter
            if (quantity == 0) {
                Button(
                    onClick = onAdd,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FlameOrange,
                        contentColor = Color.White
                    ),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        horizontal = 16.dp,
                        vertical = 2.dp
                    ),
                    modifier = Modifier
                        .height(34.dp)
                        .testTag("add_btn_${itemId}_${portion.name}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "ADD",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp
                    )
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(FlameOrange.copy(alpha = 0.15f))
                        .border(1.dp, FlameOrange, RoundedCornerShape(10.dp))
                ) {
                    IconButton(
                        onClick = onRemove,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("remove_btn_${itemId}_${portion.name}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Decrease",
                            tint = FlameOrange,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Text(
                        text = quantity.toString(),
                        color = TextPrimary,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 6.dp)
                    )

                    IconButton(
                        onClick = onAdd,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("increase_btn_${itemId}_${portion.name}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Increase",
                            tint = FlameOrange,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
