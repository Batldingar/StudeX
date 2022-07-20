package at.hagenberg.studex.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
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
 * - text-fields for adding a question and an answer
 * - radio buttons for setting a difficulty
 * - a dropdown menu for selecting a pdf
 * @param subjectID The subject id
 * @param navHostController The navigation host controller
 */
@Composable
fun NewQuestionView(subjectID: Int?, navHostController: NavHostController) {
    if (subjectID == null) return

    val context = LocalContext.current
    val pdfList = remember { mutableStateListOf<PDF>() }
    var question by remember { mutableStateOf("") }
    var answer by remember { mutableStateOf("") }
    val radioOptions = listOf("1", "2", "3")
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }
    var expandedFlag by remember { mutableStateOf(false) }
    var selectedPDF by remember { mutableStateOf<PDF?>(null) }

    loadSubjectPDFs(context, subjectID, pdfList)

    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier.padding(start = 8.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        saveQuestionForSubject(
                            context = context,
                            Question(
                                0,
                                question,
                                answer,
                                difficulty = Integer.parseInt(selectedOption),
                                pdfId = selectedPDF?.id
                            ),
                            navHostController = navHostController
                        )
                    }, colors = ButtonDefaults.buttonColors(
                        contentColor = MaterialTheme.colors.background,
                        backgroundColor = colorResource(
                            id = R.color.button_view
                        )
                    )
                ) {
                    Text(text = stringResource(R.string.question_creation_save_button))
                }

                OutlinedButton(
                    onClick = { navHostController.popBackStack() },
                    colors = ButtonDefaults.buttonColors(
                        contentColor = colorResource(
                            id = R.color.button_view
                        ), backgroundColor = MaterialTheme.colors.background
                    )
                ) {
                    Text(stringResource(R.string.question_creation_cancel_button))
                }
            }

        }
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = stringResource(R.string.question_creation_title_question),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(
                    id = R.color.text_dark
                )
            )
            TextField(
                value = question,
                onValueChange = { question = it },
                singleLine = false,
                modifier = Modifier
                    .padding(bottom = 8.dp, top = 4.dp)
                    .fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(
                    textColor = colorResource(id = R.color.text_dark),
                    focusedIndicatorColor = colorResource(id = R.color.background_view),
                    cursorColor = colorResource(id = R.color.background_view),
                    focusedLabelColor = colorResource(id = R.color.background_view)
                ),
                placeholder = { Text(stringResource(R.string.question_creation_question_placeholder)) },
                label = { Text(stringResource(R.string.question_creation_question_label)) }
            )
            TextField(
                value = answer,
                onValueChange = { answer = it },
                singleLine = false,
                modifier = Modifier
                    .padding(bottom = 8.dp, top = 4.dp)
                    .fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(
                    textColor = colorResource(id = R.color.text_dark),
                    focusedIndicatorColor = colorResource(id = R.color.background_view),
                    cursorColor = colorResource(id = R.color.background_view),
                    focusedLabelColor = colorResource(id = R.color.background_view)
                ),
                placeholder = { Text(stringResource(R.string.question_creation_answer_placeholder)) },
                label = { Text(stringResource(R.string.question_creation_answer_label)) }
            )

            Text(
                text = stringResource(R.string.question_creation_title_difficulty),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(
                    id = R.color.text_dark
                )
            )
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
                        onClick = null, // null recommended for accessibility with screenreaders
                        colors = RadioButtonDefaults.colors(
                            selectedColor = colorResource(id = R.color.background_view),
                            unselectedColor = colorResource(
                                id = R.color.text_dark
                            )
                        )
                    )
                    Text(
                        text = text,
                        style = MaterialTheme.typography.body1.merge(),
                        modifier = Modifier.padding(start = 16.dp), color = colorResource(
                            id = R.color.text_dark
                        )
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
                    selectedPDF?.let { it1 -> Text(text = it1.document_name) }

                    IconButton(onClick = { expandedFlag = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "")
                    }
                }

                DropdownMenu(
                    expanded = expandedFlag,
                    onDismissRequest = { expandedFlag = false },
                ) {
                    pdfList.forEach { pdf ->
                        DropdownMenuItem(onClick = { selectedPDF = pdf }) {
                            Text(pdf.document_name)
                        }
                    }
                }
            }
        }
    }
}

/**
 * (Re-)Loads a subject's corresponding pdfs asynchronously
 * @param context The current context
 * @param subjectID The id of the subject with the pdfs to be retrieved
 * @param pdfList The list of retrieved pdfs
 */
private fun loadSubjectPDFs(context: Context, subjectID: Int, pdfList: SnapshotStateList<PDF>) {
    CoroutineScope(Dispatchers.IO).launch {
        val subjectPDFs = AppDatabase.getInstance(context).pdfDao().getPDFsForSubjectDao(subjectID)
        withContext(Dispatchers.Main) {
            pdfList.clear()
            pdfList.addAll(subjectPDFs)
        }
    }
}

/**
 * Saves a question corresponding to a subject entry in the database asynchronously
 * @param context The current context
 * @param question The question to be saved
 * @param navHostController The navigation host controller to be used for navigation
 */
private fun saveQuestionForSubject(
    context: Context,
    question: Question,
    navHostController: NavHostController
) {
    CoroutineScope(Dispatchers.IO).launch {
        AppDatabase.getInstance(context).questionDao().insertQuestion(question)

        withContext(Dispatchers.Main) {
            Toast.makeText(
                context,
                context.getString(R.string.question_creation_save_successful),
                Toast.LENGTH_SHORT
            ).show()
            navHostController.popBackStack()
        }
    }
}