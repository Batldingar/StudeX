package at.hagenberg.studex.ui

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import at.hagenberg.studex.R
import at.hagenberg.studex.core.Subject
import at.hagenberg.studex.proxy.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * A composable consisting of:
 * - an overview of all available subjects
 * - a button for new subject creation
 * @param navController A navigation controller to be used for navigation
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SubjectView(navController: NavController) {
    val context = LocalContext.current
    val subjectList = remember { mutableStateListOf<Subject>() }
    val dialogIsVisibleFlag = remember { mutableStateOf(false) }

    loadSubjects(context, subjectList)

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { dialogIsVisibleFlag.value = true },
                backgroundColor = colorResource(id = R.color.foreground_view),
                contentColor = colorResource(id = R.color.text_light)
            ) {
                Icon(Icons.Filled.Add, "")
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) {
        LazyColumn(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            itemsIndexed(items = subjectList) { _, subject ->
                Card(modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    backgroundColor = colorResource(id = R.color.foreground_view),
                    onClick = { navController.navigate("${MainActivity.Companion.SUBJECT_DETAIL_PREFIX}${subject.id}") }
                ) {
                    subject.name?.let { it1 ->
                        Text(
                            text = it1, modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center,
                            color = colorResource(id = R.color.text_light),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        if (dialogIsVisibleFlag.value) {
            var textFieldText by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = { dialogIsVisibleFlag.value = false },
                title = {
                    Text(
                        text = stringResource(R.string.subject_addition_dialog_title),
                        color = colorResource(id = R.color.text_dark)
                    )
                },
                text = {
                    TextField(value = textFieldText,
                        onValueChange = { textFieldText = it },
                        colors = TextFieldDefaults.textFieldColors(
                            textColor = colorResource(id = R.color.text_dark),
                            focusedIndicatorColor = colorResource(id = R.color.background_view),
                            cursorColor = colorResource(id = R.color.background_view)
                        )
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            dialogIsVisibleFlag.value = false
                            CoroutineScope(Dispatchers.IO).launch {
                                AppDatabase.getInstance(context).subjectDao()
                                    .insertSubject(Subject(0, name = textFieldText))
                                loadSubjects(context, subjectList)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            contentColor = MaterialTheme.colors.background,
                            backgroundColor = colorResource(id = R.color.foreground_view)
                        )
                    ) {
                        Text(text = stringResource(R.string.subject_addition_save_button))
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { dialogIsVisibleFlag.value = false },
                        colors = ButtonDefaults.buttonColors(
                            contentColor = colorResource(id = R.color.foreground_view),
                            backgroundColor = MaterialTheme.colors.background
                        )
                    ) {
                        Text(text = stringResource(R.string.subject_addition_cancel_button))
                    }
                }
            )
        }
    }
}

/**
 * (Re-)Loads subjectList with all available subjects
 * The subject database retrieval is done asynchronously
 * @param context The current context
 * @param subjectList The list to be loaded with subjects
 */
fun loadSubjects(context: Context, subjectList: SnapshotStateList<Subject>) {
    CoroutineScope(Dispatchers.IO).launch {
        val subjects = AppDatabase.getInstance(context).subjectDao().getAll()

        withContext(Dispatchers.Main) {
            subjectList.clear()
            subjectList.addAll(subjects)
        }
    }
}

/**
 * A composable consisting of:
 * - the subject view preview
 */
@Preview(showBackground = true)
@Composable
fun SubjectViewPreview() {
    MaterialTheme {
        SubjectView(rememberNavController())
    }
}