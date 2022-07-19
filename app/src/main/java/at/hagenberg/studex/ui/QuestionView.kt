package at.hagenberg.studex.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.semantics.Role
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
fun QuestionView(subjectID: String?, navHostController: NavHostController) {
    if (subjectID == null) return

    val context = LocalContext.current
    val pdfQuestionMap = remember { mutableStateMapOf<PDF, List<Question>>() }

    if (pdfQuestionMap.size == 0) {
        loadQuestions(context, pdfQuestionMap, Integer.parseInt(subjectID))
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
            FloatingActionButton(
                onClick = { navHostController.navigate("${MainActivity.QUESTION_PREFIX}${subjectID}") },
                contentColor = MaterialTheme.colors.background,
                backgroundColor = colorResource(
                    id = R.color.button_view
                )
            ) {
                Icon(Icons.Filled.Add, "")
            }
        }) {
        Column(modifier = Modifier.padding(start = 8.dp)) {
            pdfQuestionMap.forEach { entry ->
                Text(
                    entry.key.document_name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )

                LazyColumn() {
                    itemsIndexed(items = entry.value) { index, question ->
                        Text(question.question, modifier = Modifier.padding(bottom = 4.dp))
                        Text(question.answer, fontStyle = FontStyle.Italic)
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
                AppDatabase.getInstance(context).questionDao().getQuestionsForPdf(pdf.id)
        }

        withContext(Dispatchers.Main) {
            pdfQuestionMap.putAll(bufferMap)
        }
    }
}

@Composable
fun NewQuestionView(subjectId: String?, navController: NavHostController) {
    val listPdfs = remember { mutableStateListOf<PDF>() }
    val context = LocalContext.current

    if (subjectId == null) {
        return
    }
    getPDFsForSubjec(context, Integer.parseInt(subjectId), listPdfs)

    var question by remember { mutableStateOf("") }
    var answer by remember { mutableStateOf("") }
    val radioOptions = listOf("1", "2", "3")
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }
    var expanded by remember { mutableStateOf(false) }
    var selectedPdf by remember { mutableStateOf<PDF?>(null) }

    Scaffold(
        topBar = {
            TopAppBar() {
                Text(text = "New Question")
            }
        },
        bottomBar = {
            Row(
                modifier = Modifier.padding(start = 8.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = {
                    saveQuestionForSubject(
                        context = context,
                        Question(
                            0,
                            answer,
                            question,
                            difficulty = Integer.parseInt(selectedOption),
                            pdfId = selectedPdf?.id
                        ),
                        navController = navController
                    )
                }) {
                    Text(text = "Speichern")
                }

                OutlinedButton(onClick = { navController.popBackStack() }) {
                    Text("Abbrechen")
                }
            }

        }
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = "Frage", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            TextField(
                value = question,
                onValueChange = { question = it },
                singleLine = false,
                modifier = Modifier
                    .padding(bottom = 8.dp, top = 4.dp)
                    .fillMaxWidth()
            )

            Text(text = "Antwort", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            TextField(
                value = answer,
                onValueChange = { answer = it },
                singleLine = false,
                modifier = Modifier
                    .padding(bottom = 8.dp, top = 4.dp)
                    .fillMaxWidth()
            )



            Text(text = "Schwierigkeit", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            radioOptions.forEach { text ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (text == selectedOption),
                            onClick = { onOptionSelected(text) },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (text == selectedOption),
                        onClick = null // null recommended for accessibility with screenreaders
                    )
                    Text(
                        text = text,
                        style = MaterialTheme.typography.body1.merge(),
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }

            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.TopStart)
                    .padding(top = 16.dp)
            ) {

                Row() {
                    selectedPdf?.let { it1 -> Text(text = it1.document_name) }

                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Localized description")
                    }
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    listPdfs.forEach { pdf ->
                        DropdownMenuItem(onClick = { selectedPdf = pdf }) {
                            Text(pdf.document_name)
                        }
                    }
                }
            }

            var dummyList = listOf("a", "b", "c")
            DropdownMenu(expanded = false, onDismissRequest = { /*TODO*/ }) {
                dummyList.forEach { pdf ->
                    DropdownMenuItem(onClick = { null }) {
                        Text(text = pdf)
                    }
                }
            }

        }

    }

}

private fun getPDFsForSubjec(context: Context, subjectId: Int, listPdfs: SnapshotStateList<PDF>) {
    CoroutineScope(Dispatchers.IO).launch {
        val list = AppDatabase.getInstance(context).pdfDao().getPDFsForSubjectDao(subjectId)
        withContext(Dispatchers.Main) {
            listPdfs.clear()
            listPdfs.addAll(list)
        }
    }
}

private fun saveQuestionForSubject(
    context: Context,
    question: Question,
    navController: NavHostController
) {
    CoroutineScope(Dispatchers.IO).launch {
        AppDatabase.getInstance(context).questionDao().insertQuestion(question)

        withContext(Dispatchers.Main) {
            Toast.makeText(context, "Queston was saved successfully!", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
    }
}