package at.hagenberg.studex

import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun DetailView(name: String?) {
    Column() {
        if (name != null) {
            Text(name)
        }
        OutlinedButton(onClick = {  }) {
            Text("Upload")
        }
    }

}

@Preview(showBackground = true)
@Composable
fun DetailViewPreview() {
    MaterialTheme {
        DetailView("ABC")
    }
}