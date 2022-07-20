package at.hagenberg.studex.ui

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import at.hagenberg.studex.R
import at.hagenberg.studex.core.PDF
import at.hagenberg.studex.core.Subject
import at.hagenberg.studex.proxy.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * A composable consisting of:
 * - a button for subject deletion
 * - a list of all subject corresponding pdfs
 * - a bottom navigation bar
 * @param subjectID The subject id
 * @param navHostController The navigation host controller
 */
@Composable
fun DetailView(subjectID: Int?, navHostController: NavHostController) {
    if (subjectID == null) return

    val context = LocalContext.current
    val subjectDetails = remember { mutableStateOf<Subject?>(null) }
    val pdfList = remember { mutableStateListOf<PDF>() }

    loadSubjectDetails(context, subjectID, subjectDetails, pdfList)

    Scaffold(topBar = {
        TopAppBar(backgroundColor = colorResource(id = R.color.foreground_view)) {
            subjectDetails.value?.name?.let {
                Text(
                    it, fontWeight = FontWeight.Bold, color = colorResource(
                        id = R.color.text_light
                    )
                )
            }
        }
    },
        bottomBar = {
            BottomNavigationBar(
                navHostController = navHostController,
                context = context,
                subjectID = subjectID
            )
        }) {
        Column(modifier = Modifier.padding(start = 8.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp, top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = colorResource(
                            id = R.color.button_view
                        ), backgroundColor = MaterialTheme.colors.background
                    ), onClick = {
                        subjectDetails.value?.let {
                            deleteSubject(
                                context,
                                it,
                                navHostController
                            )
                        }
                    }) {
                    Icon(Icons.Filled.Delete, contentDescription = "")
                }
            }

            if (!pdfList.isEmpty()) {
                Text(
                    stringResource(R.string.subject_pdf_list_title),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.text_dark)
                )

                LazyColumn() {
                    itemsIndexed(items = pdfList) { index, pdf ->
                        Text(
                            pdf.document_name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = colorResource(id = R.color.text_dark)
                        )
                    }
                }
            } else {
                Text(
                    stringResource(R.string.subject_pdf_list_error),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.text_dark)
                )
            }
        }
    }
}

/**
 * (Re-)Loads a subject's details including corresponding pdfs asynchronously
 * @param context The current context
 * @param subjectID The id of the subject to be retrieved
 * @param subjectDetails The subjects details to be retrieved
 * @param pdfList The list of corresponding pdfs to be retrieved
 */
private fun loadSubjectDetails(
    context: Context,
    subjectID: Int,
    subjectDetails: MutableState<Subject?>,
    pdfList: SnapshotStateList<PDF>
) {
    CoroutineScope(Dispatchers.IO).launch {
        val details = AppDatabase.getInstance(context).subjectDao().getSubjectDetails(subjectID)
        val list = AppDatabase.getInstance(context).pdfDao().getPDFsForSubjectDao(subjectID)

        withContext(Dispatchers.Main) {
            subjectDetails.value = details
            pdfList.clear()
            pdfList.addAll(list)
        }
    }
}

/**
 * Deletes a subject from the database asynchronously
 * @param context The current context
 * @param subject The subject to be deleted
 * @param navHostController The navigation host controller to be used for navigation
 */
private fun deleteSubject(
    context: Context,
    subject: Subject,
    navHostController: NavHostController
) {
    CoroutineScope(Dispatchers.IO).launch {
        AppDatabase.getInstance(context).subjectDao().delete(subject)
    }
    navHostController.popBackStack() // = analogous to pressing the back button
}

/**
 * A composable consisting of:
 * - the detail view preview
 */
@Preview(showBackground = true)
@Composable
fun DetailViewPreview() {
    MaterialTheme {
        DetailView(0, rememberNavController())
    }
}