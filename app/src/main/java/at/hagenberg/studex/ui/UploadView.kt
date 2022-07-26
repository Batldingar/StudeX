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
                // Saving a copy of the selected file in app specific storage for path persistence
                // Which is needed for longtime storage in database (uri would be temporary)
                val fileName = getFileName(context, uri)
                val stage = 0
                val persistentFileName = "$fileName${System.nanoTime()}${stage}"
                val selectedPDFInputStream =
                    context.contentResolver.openInputStream(uri)?.readBytes()

                if (selectedPDFInputStream != null) {
                    context.openFileOutput(persistentFileName, Context.MODE_PRIVATE).use {
                        it.write(selectedPDFInputStream)
                    }

                    // Uploading the pdf to the database
                    UploadView.uploadPDF(context, fileName, persistentFileName, subjectID, stage)
                }
            }

            navHostController.popBackStack()
        }

    LaunchedEffect(Unit) {
        launcher.launch("application/pdf")
    }
}

class UploadView {

    companion object {
        /**
         * Uploads a pdf file to the database asynchronously
         * @param context The current context
         * @param documentName The document's name
         * @param persistentName The document's persistent name in app specific storage
         * @param subjectID The document's corresponding subject
         * @param stage The document's stage
         */
        fun uploadPDF(
            context: Context,
            documentName: String,
            persistentName: String,
            subjectID: Int,
            stage: Int
        ) {
            val newPDF =
                PDF(
                    document_name = documentName,
                    persistent_name = persistentName,
                    subject_id = subjectID,
                    stage = stage
                )

            CoroutineScope(Dispatchers.IO).launch {
                AppDatabase.getInstance(context).pdfDao().insertPDF(newPDF)

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "PDF has been added successfully!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
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