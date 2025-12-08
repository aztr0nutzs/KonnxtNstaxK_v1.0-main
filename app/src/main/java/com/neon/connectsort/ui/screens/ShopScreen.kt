package com.neon.connectsort.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.neon.connectsort.ui.theme.*
import com.neon.connectsort.ui.screens.viewmodels.ShopViewModel

@Composable
fun ShopScreen(
    navController: NavController,
    viewModel: ShopViewModel
) {
    val shopItems by viewModel.shopItems.collectAsState()
    val playerCoins by viewModel.playerCoins.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NeonColors.neonBackground)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NeonButton(
                text = "← BACK",
                onClick = { navController.popBackStack() },
                neonColor = NeonColors.hologramBlue,
                modifier = Modifier.width(100.dp)
            )
            
            NeonText(
                text = "ARCADE SHOP",
                fontSize = 24,
                fontWeight = FontWeight.Bold,
                neonColor = NeonColors.hologramPink
            )
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "COINS",
                    color = NeonColors.textSecondary,
                    fontSize = 12.sp
                )
                NeonText(
                    text = playerCoins.toString(),
                    fontSize = 24,
                    fontWeight = FontWeight.Bold,
                    neonColor = NeonColors.hologramYellow
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Shop items
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(shopItems) { item ->
                ShopItemCard(item = item, onPurchase = { viewModel.purchaseItem(item.id) })
            }
        }
    }
}

@Composable
fun ShopItemCard(
    item: ShopItem,
    onPurchase: () -> Unit
) {
    NeonCard(
        modifier = Modifier.fillMaxWidth(),
        neonColor = if (item.isPurchased) NeonColors.hologramGreen else NeonColors.hologramBlue
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                NeonText(
                    text = item.name,
                    fontSize = 18,
                    fontWeight = FontWeight.Bold,
                    neonColor = if (item.isPurchased) NeonColors.hologramGreen else NeonColors.hologramCyan
                )
                
                Text(
                    text = item.description,
                    color = NeonColors.textSecondary,
                    fontSize = 14.sp
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Effect: ${item.effect}",
                        color = NeonColors.textSecondary,
                        fontSize = 12.sp
                    )
                }
            }
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                NeonButton(
                    text = if (item.isPurchased) "OWNED" else "${item.price} COINS",
                    onClick = onPurchase,
                    neonColor = if (item.isPurchased) NeonColors.hologramGreen else NeonColors.hologramYellow,
                    enabled = !item.isPurchased
                )
            }
        }
    }
}

data class ShopItem(
    val id: String,
    val name: String,
    val description: String,
    val price: Int,
    val effect: String,
    val isPurchased: Boolean = false
)
