package at.hagenberg.studex

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import at.hagenberg.studex.core.PDF
import at.hagenberg.studex.core.Subject
import at.hagenberg.studex.proxy.DatabaseProxy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext



@Composable
fun DetailView(subjectId: String?) {
    val subject = remember { mutableStateOf<Subject?>(null) }

    if (subjectId != null) {
        var context = LocalContext.current
        getSubjectDetails(context, Integer.parseInt(subjectId), subject)
    }

    // Subject übergeben
    Column() {
        if (subject != null) {
            subject.value?.name?.let { Text(it) }
        }
        OutlinedButton(onClick = {  }) {
            Text("Upload")
        }
        OutlinedButton(onClick = {  }) {
            Text("Remove")
        }
    }

}

fun getSubjectDetails(context: Context, subjectId: Int, subject: MutableState<Subject?>) {
    CoroutineScope(Dispatchers.IO).launch {
        val abc = DatabaseProxy.createProxy(context).subjectDao().getSubjectDetails(subjectId)
        withContext(Dispatchers.Main) {
            subject.value = abc
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetailViewPreview() {
    MaterialTheme {
        DetailView("")
    }
}