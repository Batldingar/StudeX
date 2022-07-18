package at.hagenberg.studex.ui

import android.content.Context
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.rendering.ImageType
import com.tom_roush.pdfbox.rendering.PDFRenderer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException

/**
 * A view model class for the PDFView
 */
class PDFViewModel : ViewModel() {
    val pageBitmap = MutableLiveData<ImageBitmap>()

    /**
     * Loads the first page of a pdf file
     * @param context The current context
     * @param uri The pdf's file system uri
     */
    fun loadDocument(context: Context, uri: Uri?) {
        if (uri != null) {
            val filePath = uri.path

            if (filePath != null) {
                // Run file loading using coroutine
                viewModelScope.launch(Dispatchers.IO) {
                    try {
                        // Getting the document and a renderer
                        val document = PDDocument.load(context.contentResolver.openInputStream(uri))
                        val renderer = PDFRenderer(document)

                        // Rendering an RGB image
                        pageBitmap.postValue(
                            renderer.renderImage(0, 1F, ImageType.ARGB).asImageBitmap()
                        )
                    } catch (exception: IOException) {
                        exception.printStackTrace()
                    }
                }
            }
        }
    }
}