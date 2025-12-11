package com.neon.connectsort.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.neon.connectsort.R

@Composable
fun ConnectFourArcadeBackground() {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(R.drawable.connect4_board),
            contentDescription = "Connect Four cabinet backdrop",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Image(
            painter = painterResource(R.drawable.connect4_slime),
            contentDescription = "Slime overlay",
            modifier = Modifier
                .matchParentSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.75f
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF0B0B1C).copy(alpha = 0.85f),
                            Color.Transparent
                        )
                    )
                )
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 24.dp, start = 16.dp, end = 16.dp)
                .background(Color(0xFF0B0B1C), shape = RoundedCornerShape(18.dp))
                .padding(4.dp)
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 80.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF20254A), Color(0xFF050617))
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .clip(RoundedCornerShape(24.dp))
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 28.dp, start = 12.dp, end = 12.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF1F2A3B), Color(0xFF050617))
                    ),
                    shape = RoundedCornerShape(36.dp)
                )
        )
    }
}

@Composable
fun BallSortArcadeBackground() {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(R.drawable.ballsort_board),
            contentDescription = "Ball Sort cabinet display",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.85f),
                            Color.Black.copy(alpha = 0.35f),
                            Color.Transparent
                        )
                    )
                )
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 18.dp, bottom = 18.dp, start = 12.dp, end = 12.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF1A1F3A), Color(0xFF050515))
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(2.dp)
        )
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF2D2A56), Color(0xFF15132C), Color(0xFF030410))
                    ),
                    shape = RoundedCornerShape(30.dp)
                )
                .clip(RoundedCornerShape(30.dp))
        )
    }
}
