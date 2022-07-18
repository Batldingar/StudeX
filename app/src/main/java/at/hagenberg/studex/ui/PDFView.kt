package at.hagenberg.studex.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * A composable consisting of:
 * - a button for selecting a pdf file
 * - an image displaying a pdf page
 * @param viewModel A pdf view model (populated automatically)
 */
@Composable
fun PDFView(viewModel: PDFViewModel = viewModel()) {
    val context = LocalContext.current

    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
            viewModel.loadDocument(context, uri)
        }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Button(
                onClick = {
                    launcher.launch("application/pdf")
                },
                contentPadding = PaddingValues(
                    start = 20.dp,
                    top = 12.dp,
                    end = 20.dp,
                    bottom = 12.dp
                )
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Open PDF Button",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Open PDF")
            }
        }

        val pageBitmap: ImageBitmap? by viewModel.pageBitmap.observeAsState()

        if (pageBitmap != null) {
            Image(
                bitmap = pageBitmap
                    ?: throw IllegalStateException("PDFFragment: pageBitmap is null"),
                contentDescription = "The current PDF page"
            )
        }
    }
}

/**
 * A composable consisting of:
 * - the pdf view preview
 */
@Preview(showBackground = true)
@Composable
fun PDFViewPreview() {
    MaterialTheme {
        PDFView()
    }
}