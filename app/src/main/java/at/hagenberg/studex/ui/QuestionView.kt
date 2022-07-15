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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import at.hagenberg.studex.core.PDF
import at.hagenberg.studex.core.Question
import at.hagenberg.studex.core.QuestionsOrderedByPDF
import at.hagenberg.studex.proxy.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun QuestionView(subjectId: String?, navController: NavHostController) {

    if (subjectId == null) return
    var context = LocalContext.current

    var listOfQuestions = remember { mutableStateListOf<QuestionsOrderedByPDF>() }
    var listOfQuestionsOrderedByPdf = remember { mutableStateMapOf<PDF, List<Question>>() }
    if (listOfQuestions.size == 0) {
        getQuestions(context, listOfQuestionsOrderedByPdf, Integer.parseInt(subjectId))
    }


    Scaffold(bottomBar = {
        BottomNavigation(
            navController = navController,
            context = context,
            subjectId = subjectId
        )
    },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("newQuestion/${subjectId}") }) {
                Icon(Icons.Filled.Add, "")
            }
        }) {


        Column(modifier = Modifier.padding(start = 8.dp)) {
            listOfQuestionsOrderedByPdf.forEach { entry ->
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

private fun getQuestions(
    context: Context,
    listOfQuestionsOrderedByPdf: SnapshotStateMap<PDF, List<Question>>,
    subjectId: Int
) {
    CoroutineScope(Dispatchers.IO).launch {
        var map = mutableMapOf<PDF, List<Question>>()
        val pdfs =
            AppDatabase.getInstance(context).pdfDao().getPDFsForSubjectDao(subjectId = subjectId)

        for (pdf in pdfs) {
            val questions =
                AppDatabase.getInstance(context).questionDao().getQuestionsForPdf(pdf.id)
            listOfQuestionsOrderedByPdf[pdf] = questions
        }

        withContext(Dispatchers.Main) {
            //listOfQuestions.addAll(questions)
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