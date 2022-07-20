package at.hagenberg.studex.ui

import android.content.Context
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

/**
 * A composable consisting of:
 * - a bottom navigation
 * - three bottom navigation items
 * @param context The current context
 * @param subjectID The subject id
 * @param navHostController The navigation host controller
 */
@Composable
fun BottomNavigationBar(context: Context, navHostController: NavHostController, subjectID: Int) {
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
                if (navHostController.popBackStack()) {
                    navHostController.navigate("${MainActivity.SUBJECT_DETAIL_PREFIX}${subjectID}")
                }
                navHostController.navigate("${MainActivity.PDF_UPLOAD_PREFIX}${subjectID}")
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