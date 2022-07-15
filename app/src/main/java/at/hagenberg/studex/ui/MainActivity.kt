package at.hagenberg.studex.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader

/**
 * The main activity (= UI entry point)
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setting the activity content
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    NavigationComponent(navHostController = rememberNavController())
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        // Required init call for PDFBox library
        PDFBoxResourceLoader.init(applicationContext)
    }
}

/**
 * A composable consisting of:
 * - a navigation host
 * @param navHostController A navigation host controller
 */
@Composable
fun NavigationComponent(navHostController: NavHostController) {
    // Creating a navigation host
    NavHost(navController = navHostController, startDestination = "subject_overview_screen") {
        // Adding all composables
        composable("subject_overview_screen") { SubjectView(navController = navHostController) }

        composable("subject_detail_screen/{subject_id}") { backStackEntry ->
            DetailView(backStackEntry.arguments?.getString("subject_id"), navHostController = navHostController)
        }

        composable("pdf_selection_screen") { PDFView() }
        //TODO: Cleanup of the following lines
        composable("questions/{itemId}") { backStackEntry ->
            QuestionView(
                subjectId = backStackEntry.arguments?.getString("itemId"),
                navController = navHostController
            )
        }

        composable("newQuestion/{itemId}") { backStackEntry ->
            NewQuestionView(
                backStackEntry.arguments?.getString("itemId"),
                navController = navHostController
            )
        }
    }
}

/**
 * A composable consisting of:
 * - the main view preview
 */
@Preview(showBackground = true)
@Composable
fun MainViewPreview() {
    MaterialTheme {
        NavigationComponent(navHostController = rememberNavController())
    }
}