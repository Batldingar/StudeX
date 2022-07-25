package at.hagenberg.studex.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import at.hagenberg.studex.R

/**
 * A composable consisting of:
 * - a button for selecting a pdf file
 * - an image displaying a pdf page
 * @param pdfID The pdf id
 * @param navHostController The navigation host controller
 * @param viewModel A pdf view model (populated automatically)
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PDFView(
    pdfID: Int?,
    navHostController: NavHostController,
    viewModel: PDFViewModel = viewModel()
) {
    val context = LocalContext.current
    val bitmapList: ArrayList<ImageBitmap>? by viewModel.bitmapList.observeAsState()
    val progress: Float? by viewModel.loadingProgress.observeAsState()
    val selectedCardList = remember { mutableStateListOf<Int>() }

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {

        if (bitmapList != null) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                bitmapList?.let { it1 ->
                    itemsIndexed(items = it1) { index, bitmap ->
                        Card(
                            onClick = {
                                if (!selectedCardList.contains(index)) selectedCardList.add(
                                    index
                                ) else selectedCardList.remove(index)
                            },
                            modifier = if (selectedCardList.contains(index)) Modifier
                                .fillMaxWidth()
                                .border(2.dp, colorResource(id = R.color.button_view))
                            else Modifier.fillMaxWidth()
                        ) {
                            Image(
                                bitmap = bitmap,
                                modifier = Modifier.fillMaxWidth(),
                                alignment = Alignment.Center,
                                contentScale = ContentScale.FillWidth,
                                contentDescription = "The current PDF page"
                            )
                        }
                    }
                }
            }
        } else {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                progress?.let { it1 ->
                    LinearProgressIndicator(
                        progress = it1,
                        color = colorResource(id = R.color.foreground_view)
                    )
                }
            }
        }

        DisposableEffect(key1 = viewModel) {
            viewModel.loadDocument(context, pdfID)
            onDispose { viewModel.onDispose() }
        }
    }
}