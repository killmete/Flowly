package com.example.flowly.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import java.text.NumberFormat
import java.util.Locale

@Composable
fun AnimatedCounter(
    value: Double,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.displaySmall,
    color: Color = MaterialTheme.colorScheme.onBackground,
    prefix: String = "$",
    showSign: Boolean = false
) {
    val formatter = NumberFormat.getNumberInstance(Locale.US).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }
    val sign = when {
        showSign && value > 0 -> "+"
        value < 0 -> "-"
        else -> ""
    }
    val formatted = "$sign$prefix${formatter.format(kotlin.math.abs(value))}"

    AnimatedContent(
        targetState = formatted,
        modifier = modifier,
        transitionSpec = {
            (slideInVertically { it } + fadeIn()).togetherWith(
                slideOutVertically { -it } + fadeOut()
            )
        },
        label = "counter"
    ) { text ->
        Text(
            text = text,
            style = style,
            color = color
        )
    }
}
