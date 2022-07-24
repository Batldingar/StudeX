package at.hagenberg.studex.ui

import android.content.Context
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

/**
 * A view model class for the PDFView
 */
class PDFViewModel : ViewModel() {
    val bitmapList = MutableLiveData<ArrayList<ImageBitmap>>()
    val loadingProgress = MutableLiveData<Float>()

    /**
     * Loads the first page of a pdf file
     * @param context The current context
     * @param pdfID The pdf's id
     */
    fun loadDocument(context: Context, pdfID: Int?) {
        if (pdfID == null) return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Getting the document and a renderer
                val document =
                    PDDocument.load(context.openFileInput(loadPDF(context, pdfID).persistent_name))
                val renderer = PDFRenderer(document)

                // Rendering ARGB images
                val renderedBitmapList: ArrayList<ImageBitmap> = ArrayList()

                for (i in 0 until document.numberOfPages) {
                    loadingProgress.postValue(i.toFloat() / document.numberOfPages)
                    renderedBitmapList.add(
                        renderer.renderImage(i, 1F, ImageType.ARGB).asImageBitmap()
                    )
                }

                bitmapList.postValue(renderedBitmapList)

            } catch (exception: IOException) {
                exception.printStackTrace()
            }
        }
    }

    /**
     * (Re-)Loads a pdf asynchronously
     * @param context The current context
     * @param pdfID The id of the pdf to be retrieved
     * @param pdf The pdf to be retrieved
     */
    private suspend fun loadPDF(
        context: Context,
        pdfID: Int,
    ): PDF = withContext(Dispatchers.IO) {
        return@withContext AppDatabase.getInstance(context).pdfDao().getPDF(pdfID)
    }
}