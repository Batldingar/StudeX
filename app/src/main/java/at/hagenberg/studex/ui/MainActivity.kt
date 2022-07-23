package at.hagenberg.studex.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
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

    companion object {
        const val SUBJECT_OVERVIEW_ROUTE = "subject_overview_screen"
        const val PARAMETER_ID = "parameter_id"
        const val SUBJECT_DETAIL_PREFIX = "subject_detail_screen/"
        const val SUBJECT_DETAIL_ROUTE = "$SUBJECT_DETAIL_PREFIX{$PARAMETER_ID}"
        const val PDF_UPLOAD_PREFIX = "pdf_upload_screen/"
        const val PDF_UPLOAD_ROUTE = "$PDF_UPLOAD_PREFIX{$PARAMETER_ID}"
        const val QUESTION_PREFIX = "question_screen/"
        const val QUESTION_ROUTE = "$QUESTION_PREFIX{$PARAMETER_ID}"
        const val QUESTION_CREATION_PREFIX = "question_creation_screen/"
        const val QUESTION_CREATION_ROUTE = "$QUESTION_CREATION_PREFIX{$PARAMETER_ID}"
        const val PDF_DETAIL_PREFIX = "pdf_detail_screen/"
        const val PDF_DETAIL_ROUTE = "$PDF_DETAIL_PREFIX{$PARAMETER_ID}"
    }

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
    NavHost(
        navController = navHostController,
        startDestination = MainActivity.SUBJECT_OVERVIEW_ROUTE
    ) {
        // Adding all composables
        composable(MainActivity.SUBJECT_OVERVIEW_ROUTE) { SubjectView(navController = navHostController) }

        composable(MainActivity.SUBJECT_DETAIL_ROUTE) { backStackEntry ->
            DetailView(
                subjectID = Integer.parseInt(
                    backStackEntry.arguments?.getString(MainActivity.PARAMETER_ID).toString()
                ),
                navHostController = navHostController
            )
        }

        composable(MainActivity.PDF_UPLOAD_ROUTE) { backStackEntry ->
            UploadView(
                subjectID = Integer.parseInt(
                    backStackEntry.arguments?.getString(MainActivity.PARAMETER_ID).toString()
                ),
                navHostController = navHostController
            )
        }

        composable(MainActivity.QUESTION_ROUTE) { backStackEntry ->
            QuestionView(
                subjectID = Integer.parseInt(
                    backStackEntry.arguments?.getString(MainActivity.PARAMETER_ID).toString()
                ),
                navHostController = navHostController
            )
        }

        composable(MainActivity.QUESTION_CREATION_ROUTE) { backStackEntry ->
            NewQuestionView(
                subjectID = Integer.parseInt(
                    backStackEntry.arguments?.getString(MainActivity.PARAMETER_ID).toString()
                ),
                navHostController = navHostController
            )
        }

        composable(MainActivity.PDF_DETAIL_ROUTE) { backStackEntry ->
            PDFView(
                pdfID = Integer.parseInt(
                    backStackEntry.arguments?.getString(MainActivity.PARAMETER_ID).toString()
                ),
                navHostController = navHostController
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