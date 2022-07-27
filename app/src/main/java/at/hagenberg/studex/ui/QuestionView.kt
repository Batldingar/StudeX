package at.hagenberg.studex.ui

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import at.hagenberg.studex.R
import at.hagenberg.studex.core.PDF
import at.hagenberg.studex.core.Question
import at.hagenberg.studex.proxy.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * A composable consisting of:
 * - a button for question addition
 * - a list of all subject corresponding pdfs and their corresponding questions
 * - a bottom navigation bar
 * @param subjectID The subject id
 * @param navHostController The navigation host controller
 */
@Composable
fun QuestionView(subjectID: Int?, navHostController: NavHostController) {
    if (subjectID == null) return

    val context = LocalContext.current
    val pdfQuestionMap = remember { mutableStateMapOf<PDF, List<Question>>() }

    if (pdfQuestionMap.size == 0) {
        loadQuestions(context, pdfQuestionMap, subjectID)
    }

    Scaffold(bottomBar = {
        BottomNavigationBar(
            navHostController = navHostController,
            context = context,
            subjectID = subjectID
        )
    },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            if (!pdfQuestionMap.isEmpty()) {
                FloatingActionButton(
                    onClick = { navHostController.navigate("${MainActivity.QUESTION_CREATION_PREFIX}${subjectID}") },
                    contentColor = MaterialTheme.colors.background,
                    backgroundColor = colorResource(
                        id = R.color.button_view
                    )
                ) {
                    Icon(Icons.Filled.Add, "")
                }
            }
        }) {
        Column(modifier = Modifier.padding(start = 8.dp)) {
            pdfQuestionMap.forEach { entry ->
                Text(
                    entry.key.document_name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )

                LazyColumn() {
                    itemsIndexed(items = entry.value) { index, question ->
                        Text(
                            "${index + 1}) ${question.question}",
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Italic
                        )
                        Text(question.answer, modifier = Modifier.padding(bottom = 8.dp))
                    }
                }
            }
        }
    }
}

/**
 * (Re-)Loads all pdfs and their corresponding questions for the given subject
 * @param context The current context
 * @param pdfQuestionMap The map of pdfs and corresponding questions
 * @param subjectID The id of the subject with the pdf and questions to be retrieved
 */
private fun loadQuestions(
    context: Context,
    pdfQuestionMap: SnapshotStateMap<PDF, List<Question>>,
    subjectID: Int
) {
    CoroutineScope(Dispatchers.IO).launch {
        val bufferMap: SnapshotStateMap<PDF, List<Question>> = SnapshotStateMap()

        val pdfList =
            AppDatabase.getInstance(context).pdfDao().getPDFsForSubjectDao(subjectId = subjectID)

        for (pdf in pdfList) {
            bufferMap[pdf] =
                AppDatabase.getInstance(context).questionDao().getQuestionsForPdf(pdf.document_name)
        }

        withContext(Dispatchers.Main) {
            pdfQuestionMap.putAll(bufferMap)
        }
    }
}