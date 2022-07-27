package at.hagenberg.studex.proxy

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import at.hagenberg.studex.core.PDF

@Dao
interface PDFDao {

    @Query("SELECT * FROM pdfs WHERE pdfs.subject_id = :subjectId AND pdfs.stage = 0")
    fun getPDFsForSubjectDao(subjectId: Int): List<PDF>

    @Query("SELECT * FROM pdfs WHERE pdfs.document_name = :pdfName AND pdfs.subject_id = :subjectID AND pdfs.stage = :pdfStage")
    fun getPDF(pdfName: String, subjectID: Int, pdfStage: Int): PDF

    @Query("SELECT COUNT(*) FROM pdfs WHERE pdfs.document_name = :pdfName AND pdfs.subject_id = :subjectID")
    fun getPDFStageCount(pdfName: String, subjectID: Int): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertPDF(vararg pdf: PDF)
}