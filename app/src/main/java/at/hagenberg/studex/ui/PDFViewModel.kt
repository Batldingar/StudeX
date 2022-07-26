package at.hagenberg.studex.ui

import android.content.Context
import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.hagenberg.studex.core.PDF
import at.hagenberg.studex.proxy.AppDatabase
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.rendering.ImageType
import com.tom_roush.pdfbox.rendering.PDFRenderer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

/**
 * A view model class for the PDFView
 */
class PDFViewModel : ViewModel() {
    val pdf = MutableLiveData<PDF>()
    val pageCount = MutableLiveData<Int>()
    val stage = MutableLiveData<Int>()
    val stageCount = MutableLiveData<Int>()
    val bitmapList = MutableLiveData<ArrayList<ImageBitmap>>()
    val loadingProgress = MutableLiveData<Float>()
    private var document: PDDocument = PDDocument()
    private var loadedPDF: PDF = PDF("", "", 0, 0)

    /**
     * Loads the first page of a pdf file
     * @param context The current context
     * @param pdfName The pdf's name
     * @param subjectID The subject id
     * @param pdfStage The pdf's stage
     */
    fun loadDocument(context: Context, pdfName: String?, subjectID: Int, pdfStage: Int) {
        if (pdfName == null) return

        viewModelScope.launch(Dispatchers.IO) {
            loadedPDF = loadPDF(context, pdfName, subjectID, pdfStage)

            val pdfStageCount = loadStageCount(context, pdfName, subjectID)

            try {
                // Getting the document and a renderer
                document =
                    PDDocument.load(context.openFileInput(loadedPDF.persistent_name))
                val renderer = PDFRenderer(document)

                // Rendering ARGB images
                val renderedBitmapList: ArrayList<ImageBitmap> = ArrayList()

                for (i in 0 until document.numberOfPages) {
                    ensureActive()
                    loadingProgress.postValue(i.toFloat() / document.numberOfPages)
                    renderedBitmapList.add(
                        renderer.renderImage(i, 1F, ImageType.ARGB).asImageBitmap()
                    )
                }

                document.close()

                bitmapList.postValue(renderedBitmapList)

            } catch (exception: IOException) {
                exception.printStackTrace()
            }

            withContext(Dispatchers.Main) {
                pdf.value = loadedPDF
                pageCount.value = document.numberOfPages
                stage.value = pdfStage
                stageCount.value = pdfStageCount
            }
        }
    }

    /**
     * Saves a cut down codument
     * @param context The current context
     * @param pdfName The pdf's name
     * @param subjectID The subject id
     * @param pdfStage The pdf's highest stage
     * @param pagesToBeCut A list of pages to be cut
     */
    fun saveCutDocument(
        context: Context,
        pdfName: String?,
        subjectID: Int,
        pdfStage: Int,
        pagesToBeCut: SnapshotStateList<Int>
    ) {
        if (pdfName == null) return

        viewModelScope.launch(Dispatchers.IO) {

            loadedPDF = loadPDF(context, pdfName, subjectID, pdfStage)
            Log.d("Tag", "$pdfName, $subjectID, $pdfStage")

            try {
                // Getting the document and a renderer
                val document =
                    PDDocument.load(context.openFileInput(loadedPDF.persistent_name))

                for (pageToBeCut: Int in pagesToBeCut) {
                    ensureActive()
                    document.removePage(pageToBeCut)
                }

                val newStage = loadedPDF.stage + 1
                val newPersistentName = loadedPDF.persistent_name.dropLast(1) + "$newStage"

                document.save(context.openFileOutput(newPersistentName, Context.MODE_PRIVATE))

                UploadView.uploadPDF(
                    context,
                    loadedPDF.document_name,
                    newPersistentName,
                    loadedPDF.subject_id,
                    newStage
                )

                document.close()
            } catch (exception: IOException) {
                exception.printStackTrace()
            }

            withContext(Dispatchers.Main) {
                loadDocument(context, pdfName, subjectID, pdfStage + 1)
                pagesToBeCut.clear()
            }
        }
    }

    /**
     * (Re-)Loads a pdf asynchronously
     * @param context The current context
     * @param pdfName The name of the pdf to be retrieved
     * @param subjectID The subject id
     * @param pdfStage The stage of the pdf to be retrieved
     * @return The retrieved pdf
     */
    private suspend fun loadPDF(
        context: Context,
        pdfName: String,
        subjectID: Int,
        pdfStage: Int
    ): PDF = withContext(Dispatchers.IO) {
        return@withContext AppDatabase.getInstance(context).pdfDao()
            .getPDF(pdfName, subjectID, pdfStage)
    }

    /**
     * (Re-)Loads the amount of stages of a pdf asynchronously
     * @param context The current context
     * @param subjectID The subject id
     * @param pdfName The name of the pdf stage count to be retrieved
     * @return The retrieved pdf stage count
     */
    private suspend fun loadStageCount(
        context: Context,
        pdfName: String,
        subjectID: Int,
    ): Int = withContext(Dispatchers.IO) {
        return@withContext AppDatabase.getInstance(context).pdfDao()
            .getPDFStageCount(pdfName, subjectID)
    }

    /**
     * Closes all resources that need to be closed when exiting the overlying composable
     */
    fun onDispose() {
        document.close()
    }
}