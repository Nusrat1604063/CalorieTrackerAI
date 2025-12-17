package com.example.calorietrack.Presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun FloatingBottomBar(
    selectedTab: BottomBarTab = BottomBarTab.Home,
    onTabSelected: (BottomBarTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Card(
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF2E5C56).copy(alpha = 0.95f) // ← Soft green container
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .align(Alignment.BottomCenter)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomBarItem(
                    tab = BottomBarTab.Home,
                    selected = selectedTab == BottomBarTab.Home,
                    onClick = { onTabSelected(BottomBarTab.Home) },
                    icon = Icons.Default.Home,
                    label = "Home"
                )

                BottomBarItem(
                    tab = BottomBarTab.Scan,
                    selected = selectedTab == BottomBarTab.Scan,
                    onClick = { onTabSelected(BottomBarTab.Scan) },
                    icon = Icons.Filled.AddCircle,
                    label = "Scan",
                    isHighlighted = true
                )

                BottomBarItem(
                    tab = BottomBarTab.About,
                    selected = selectedTab == BottomBarTab.About,
                    onClick = { onTabSelected(BottomBarTab.About) },
                    icon = Icons.Default.Info,
                    label = "About"
                )
            }
        }
    }
}

// Updated BottomBarItem – white icons/text, green highlight for selected/Scan
@Composable
private fun BottomBarItem(
    tab: BottomBarTab,
    selected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector,
    label: String,
    isHighlighted: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color.White, // ← Always white
            modifier = Modifier
                .size(if (isHighlighted || selected) 32.dp else 26.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = if (isHighlighted || selected) 13.sp else 11.sp,
            fontWeight = if (selected || isHighlighted) FontWeight.SemiBold else FontWeight.Normal,
            color = Color.White // ← Always white
        )
    }
}
enum class BottomBarTab { Home, Scan, About }


@Preview(showBackground = true)
@Composable()
fun ShowbottomBar() {
   FloatingBottomBar(onTabSelected = {})
}
