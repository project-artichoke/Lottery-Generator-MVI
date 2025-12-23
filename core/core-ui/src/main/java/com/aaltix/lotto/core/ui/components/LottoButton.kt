package com.aaltix.lotto.core.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aaltix.lotto.core.ui.R
import com.aaltix.lotto.core.ui.animation.shakeAnimation
import com.aaltix.lotto.core.ui.theme.LottoTheme

/**
 * Primary button for the Lotto app with optional loading state and shake animation.
 *
 * @param text Button text
 * @param onClick Click handler
 * @param modifier Modifier
 * @param enabled Whether the button is enabled
 * @param isLoading Whether to show loading indicator
 * @param icon Optional leading icon
 */
@Composable
fun LottoButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    icon: ImageVector? = null
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .shakeAnimation(enabled = isLoading),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.generating),
                style = MaterialTheme.typography.titleMedium
            )
        } else {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LottoButtonPreview() {
    LottoTheme {
        LottoButton(
            text = "Generate Numbers",
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LottoButtonLoadingPreview() {
    LottoTheme {
        LottoButton(
            text = "Generate Numbers",
            onClick = {},
            isLoading = true
        )
    }
}
