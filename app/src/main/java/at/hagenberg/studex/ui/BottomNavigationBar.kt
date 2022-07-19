package at.hagenberg.studex.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import at.hagenberg.studex.R
import at.hagenberg.studex.core.PDF
import at.hagenberg.studex.proxy.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * A composable consisting of:
 * - a bottom navigation
 * - three bottom navigation items
 * @param context The current context
 * @param subjectID The subject id
 * @param navHostController The navigation host controller
 */
@Composable
fun BottomNavigationBar(context: Context, navHostController: NavHostController, subjectID: String) {
    androidx.compose.material.BottomNavigation(
        contentColor = MaterialTheme.colors.background, backgroundColor = colorResource(
            id = R.color.foreground_view
        )
    ) {
        val backStackEntry by navHostController.currentBackStackEntryAsState()
        val currentRoute = backStackEntry?.destination?.route

        BottomNavigationItem(selected = currentRoute == MainActivity.SUBJECT_DETAIL_ROUTE,
            onClick = {
                if (navHostController.popBackStack()) {
                    navHostController.navigate("${MainActivity.SUBJECT_DETAIL_PREFIX}${subjectID}")
                }
            },
            label = { Text(stringResource(R.string.detail_view_bottom_navigation_overview)) },
            icon = { Icon(Icons.Filled.Home, contentDescription = null) })
        BottomNavigationItem(selected = false,
            onClick = {
                addDummyPDF(context, Integer.parseInt(subjectID))
                // TODO: Position the following line after navigating to and finishing the upload
                if (navHostController.popBackStack()) {
                    navHostController.navigate("${MainActivity.SUBJECT_DETAIL_PREFIX}${subjectID}")
                }
            },
            label = { Text(stringResource(R.string.detail_view_bottom_navigation_upload)) },
            icon = { Icon(Icons.Filled.Add, contentDescription = null) })
        BottomNavigationItem(selected = currentRoute == MainActivity.QUESTION_ROUTE,
            onClick = {
                if (navHostController.popBackStack()) {
                    navHostController.navigate("${MainActivity.QUESTION_PREFIX}${subjectID}")
                }
            },
            label = { Text(stringResource(R.string.detail_view_bottom_navigation_questions)) },
            icon = { Icon(Icons.Filled.List, contentDescription = null) })
    }
}

/**
 * Adds a dummy pdf to the database
 * @param context The current context
 * @param subjectID The id of the subject to be added
 */
private fun addDummyPDF(context: Context, subjectID: Int) {
    val newPDF =
        PDF(id = 0, document_name = "DummyPDF", file_path = "abcdef", subject_id = subjectID)

    CoroutineScope(Dispatchers.IO).launch {
        AppDatabase.getInstance(context).pdfDao().insertPDF(newPDF)

        withContext(Dispatchers.Main) {
            Toast.makeText(context, "PDF has been added successfully!", Toast.LENGTH_SHORT).show()
        }
    }
}