package com.wojbeg.aws_travelnotes.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun RoundImage(
    image: Painter,
    description: String? = null,
    borderWidth: Dp = 1.dp,
    borderColor: Color = Color.White,
    clickable: Boolean = false,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
   var newModifier = modifier
        .aspectRatio(1f, matchHeightConstraintsFirst = true)
        .border(
            width = borderWidth,
            color = borderColor,
            shape = CircleShape
        )
        .padding(3.dp)
        .clip(CircleShape)

   if (clickable) {
       newModifier = newModifier.clickable { onClick() }
   }

   Image(
       painter = image,
       contentDescription = description,
       modifier = newModifier
   )
}