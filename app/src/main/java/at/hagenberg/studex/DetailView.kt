package at.hagenberg.studex

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import at.hagenberg.studex.core.PDF
import at.hagenberg.studex.core.Subject
import at.hagenberg.studex.proxy.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.intellij.lang.annotations.JdkConstants

@Composable
fun DetailView(subjectId: String?, navController: NavHostController) {
    val subject = remember { mutableStateOf<Subject?>(null) }
    val listPdfs = remember { mutableStateListOf<PDF>() }

    if(subjectId == null) return

    var context = LocalContext.current
    getSubjectAndPDFDetails(context, Integer.parseInt(subjectId), subject, listPdfs)

    Scaffold(topBar = { TopAppBar() {
        subject.value?.name?.let { Text(it, fontWeight = FontWeight.Bold) }
    }},
        bottomBar = { BottomNavigation(navController = navController, context = context, subjectId = subjectId) }) {
        Column(modifier = Modifier.padding(start = 8.dp)){

            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp, top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp) ) {

                OutlinedButton(onClick = { subject.value?.let { deleteSubject(context, it, navController) } }) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete")
                }
            }


            Text("PDFs:", fontSize = 16.sp, fontWeight = FontWeight.Bold)

            LazyColumn() {
                itemsIndexed(items = listPdfs) { index, pdf ->

                    Text(pdf.document_name)
                }
            }



        }
    }
}

private fun deleteSubject(context: Context, subject: Subject, navController: NavHostController) {
    CoroutineScope(Dispatchers.IO).launch {
        AppDatabase.getInstance(context).subjectDao().delete(subject)
    }
    navController.popBackStack()
}



private fun getSubjectAndPDFDetails(context: Context, subjectId: Int, subject: MutableState<Subject?>, listPdfs: SnapshotStateList<PDF>) {
    CoroutineScope(Dispatchers.IO).launch {
        val details = AppDatabase.getInstance(context).subjectDao().getSubjectDetails(subjectId)
        val list = AppDatabase.getInstance(context).pdfDao().getPDFsForSubjectDao(subjectId)
        withContext(Dispatchers.Main) {
            subject.value = details
            listPdfs.clear()
            listPdfs.addAll(list)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetailViewPreview() {
    MaterialTheme {
        val navController = rememberNavController()
        DetailView("", navController)
    }
}