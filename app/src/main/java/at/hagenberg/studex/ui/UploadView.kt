package at.hagenberg.studex.ui

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import at.hagenberg.studex.core.PDF
import at.hagenberg.studex.proxy.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * A composable consisting of:
 * - a button for selecting a pdf file
 * - an image displaying a pdf page
 * @param subjectID The subject with the pdf to be uploaded
 */
@Composable
fun UploadView(subjectID: Int?, navHostController: NavHostController) {
    if (subjectID == null) return

    val context = LocalContext.current

    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                uploadPDF(context, getFileName(context, uri), uri.toString(), subjectID)
            }

            navHostController.popBackStack()
        }

    LaunchedEffect(Unit) {
        launcher.launch("application/pdf")
    }
}

/**
 * Uploads a pdf file to the database asynchronously
 * @param context The current context
 * @param documentName The document's name
 * @param fileURI The document's uri
 * @param subjectID The document's corresponding subject
 */
private fun uploadPDF(context: Context, documentName: String, fileURI: String, subjectID: Int) {
    val newPDF =
        PDF(id = 0, document_name = documentName, file_uri = fileURI, subject_id = subjectID)

    CoroutineScope(Dispatchers.IO).launch {
        AppDatabase.getInstance(context).pdfDao().insertPDF(newPDF)

        withContext(Dispatchers.Main) {
            Toast.makeText(context, "PDF has been added successfully!", Toast.LENGTH_SHORT).show()
        }
    }
}

/**
 * Retrieves a file name from a uri
 * @param context The current context
 * @param uri The uri corresponding to the file with the name to be retrieved
 */
fun getFileName(context: Context, uri: Uri): String {
    var fileName: String = "Unknown"
    val cursor = context.contentResolver.query(uri, null, null, null, null)

    if (cursor != null) {
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        cursor.moveToFirst()
        fileName = cursor.getString(nameIndex)
        cursor.close()
    }

    return fileName
}