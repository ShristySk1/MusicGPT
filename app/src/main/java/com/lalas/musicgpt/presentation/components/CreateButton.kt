package com.lalas.musicgpt.presentation.components
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lalas.musicgpt.R

@Composable
fun CreateButton(
    onShowInputChange: (Boolean) -> Unit,
) {
            Surface(
                onClick = { onShowInputChange(true) },
                shape = RoundedCornerShape(25.dp),
                contentColor = Color.White,
                color = Color(0xE6000000),
                shadowElevation = 16.dp
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(color = Color(0x1AFFFFFF))
                        .padding(
                        top = 8.dp,
                        bottom = 8.dp,
                        start = 16.dp,
                        end = 18.dp
                    )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_star),
                        contentDescription = null,
                        tint = Color.Unspecified
                    )
                    Text(
                        "Create",
                        fontSize = 16.sp
                    )
                }
    }
}
@Preview(showBackground = true)
@Composable
fun CreateButtonPreview() {
    // You can wrap it in your app's theme if needed
    MaterialTheme {
        CreateButton(
            onShowInputChange = { /* Preview - no action needed */ }
        )
    }
}

