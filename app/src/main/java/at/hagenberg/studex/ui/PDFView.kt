package at.hagenberg.studex.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

/**
 * A composable consisting of:
 * - a button for selecting a pdf file
 * - an image displaying a pdf page
 * @param pdfID The pdf id
 * @param navHostController The navigation host controller
 * @param viewModel A pdf view model (populated automatically)
 */
@Composable
fun PDFView(
    pdfID: Int?,
    navHostController: NavHostController,
    viewModel: PDFViewModel = viewModel()
) {
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        LaunchedEffect(Unit) {
            viewModel.loadDocument(context, pdfID)
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