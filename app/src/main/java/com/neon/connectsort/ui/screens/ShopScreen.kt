package com.neon.connectsort.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
    
    Box(modifier = Modifier.fillMaxSize()) {
        HolographicParticleSystem()
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
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.width(100.dp).holographicBorder()
                ) {
                    Text(text = "← BACK", style = MaterialTheme.typography.labelMedium)
                }
                
                Text(
                    text = "ARCADE SHOP",
                    style = MaterialTheme.typography.headlineMedium,
                    color = NeonColors.hologramPink
                )
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "COINS",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonColors.textSecondary
                    )
                    Text(
                        text = playerCoins.toString(),
                        style = MaterialTheme.typography.headlineSmall,
                        color = NeonColors.hologramYellow
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
}

@Composable
fun ShopItemCard(
    item: ShopItem,
    onPurchase: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth().holographicBorder()
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
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (item.isPurchased) NeonColors.hologramGreen else NeonColors.hologramCyan
                )
                
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = NeonColors.textSecondary
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Effect: ${item.effect}",
                        style = MaterialTheme.typography.bodySmall,
                        color = NeonColors.textSecondary
                    )
                }
            }
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Button(
                    onClick = onPurchase,
                    modifier = Modifier.holographicBorder(),
                    enabled = !item.isPurchased
                ) {
                    Text(
                        text = if (item.isPurchased) "OWNED" else "${item.price} COINS", 
                        style = MaterialTheme.typography.labelMedium
                    )
                }
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
