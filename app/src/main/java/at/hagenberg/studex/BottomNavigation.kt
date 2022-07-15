package at.hagenberg.studex

import android.content.Context
import android.widget.Toast
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import at.hagenberg.studex.core.PDF
import at.hagenberg.studex.proxy.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun BottomNavigation(navController: NavHostController, context: Context, subjectId: String) {
    // TODO geht nd aber is ma ehrlich gsagt wurscht
    var selectedItem by remember { mutableStateOf(0) }

    androidx.compose.material.BottomNavigation() {
        BottomNavigationItem(selected = selectedItem == 0,
            onClick = {
                selectedItem = 0
                navController.navigate("showDetails/${subjectId}") },
            label = { Text("Overview") },
            icon = { Icon(Icons.Filled.Favorite, contentDescription = null) })
        // TODO add here upload
        BottomNavigationItem(selected = selectedItem == 1,
            onClick = {
                selectedItem = 1
                addDummyPDF(Integer.parseInt(subjectId), context) },
            label = { Text("Upload PDF") },
            icon = { Icon(Icons.Filled.Favorite, contentDescription = null) })
        BottomNavigationItem(selected = selectedItem == 2,
            onClick = {
                selectedItem = 2
                navController.navigate("questions/${subjectId}") },
            label = { Text("Questions") },
            icon = { Icon(Icons.Filled.Favorite, contentDescription = null) })
    }
}

private fun addDummyPDF(subjectId: Int, context: Context) {
    var pdf = PDF(id = 0, document_name = "DummyPDF", file_path = "abcdef", subject_id = subjectId)

    CoroutineScope(Dispatchers.IO).launch {
        AppDatabase.getInstance(context).pdfDao().insertPDF(pdf)

        withContext(Dispatchers.Main) {
            Toast.makeText(context, "PDF was created successfully!", Toast.LENGTH_SHORT).show()
        }
    }

}