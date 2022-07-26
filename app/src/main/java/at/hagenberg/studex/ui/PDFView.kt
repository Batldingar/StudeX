package at.hagenberg.studex.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import at.hagenberg.studex.R
import at.hagenberg.studex.core.PDF

/**
 * A composable consisting of:
 * - a top bar displaying pdf information
 * - an lazy column displaying a pdf
 * @param pdfID The pdf id
 * @param subjectID The subject id
 * @param navHostController The navigation host controller
 * @param viewModel A pdf view model (populated automatically)
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PDFView(
    pdfName: String?,
    subjectID: Int,
    navHostController: NavHostController,
    viewModel: PDFViewModel = viewModel()
) {
    val context = LocalContext.current
    val pdf: PDF? by viewModel.pdf.observeAsState()
    val pageCount: Int? by viewModel.pageCount.observeAsState()
    val stage: Int? by viewModel.stage.observeAsState()
    val stageCount: Int? by viewModel.stageCount.observeAsState()
    val bitmapList: ArrayList<ImageBitmap>? by viewModel.bitmapList.observeAsState()
    val progress: Float? by viewModel.loadingProgress.observeAsState()
    val selectedCardList = remember { mutableStateListOf<Int>() }

    Scaffold(topBar = {
        TopAppBar(backgroundColor = colorResource(id = R.color.foreground_view)) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                pdf?.document_name?.let {
                    Text(
                        it,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(
                            id = R.color.text_light
                        )
                    )
                }
                Spacer(modifier = Modifier.weight(1F))
                stage?.let {
                    Text(
                        "Stage ${it + 1} / $stageCount",
                        fontWeight = FontWeight.Bold,
                        color = colorResource(
                            id = R.color.text_light
                        )
                    )
                }
            }
        }
    }, bottomBar = {
        BottomAppBar(backgroundColor = colorResource(id = R.color.foreground_view)) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                pageCount?.let {
                    Text(
                        "${selectedCardList.size} / $it ${stringResource(R.string.pdf_view_removal_selection)}",
                        fontWeight = FontWeight.Bold,
                        color = colorResource(
                            id = R.color.text_light
                        )
                    )
                }
                Spacer(modifier = Modifier.weight(1F))
                if (pageCount != null) {
                    if (!selectedCardList.isEmpty()) {
                        Button(
                            onClick = {
                                stageCount?.let {
                                    viewModel.saveCutDocument(
                                        context, pdfName, subjectID, it - 1, selectedCardList
                                    )
                                }
                            },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = colorResource(
                                    id = R.color.button_view
                                ), backgroundColor = MaterialTheme.colors.background
                            ),
                        ) {
                            Text(
                                stringResource(R.string.pdf_view_new_stage_button),
                                color = colorResource(
                                    id = R.color.button_view
                                ),

                                )
                            Icon(Icons.Filled.Add, contentDescription = "")
                        }
                    }
                }
            }
        }
    }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                viewModel.loadDocument(context, pdfName, subjectID, 0)
                onDispose { viewModel.onDispose() }
            }
        }
    }
}