package at.hagenberg.studex.proxy

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import at.hagenberg.studex.core.PDF

@Dao
interface PDFDao {

    @Query("SELECT * FROM pdfs")
    fun getAllPDFs(): List<PDF>

    @Query("SELECT * FROM pdfs WHERE pdfs.subject_id = :subjectId")
    fun getPDFsForSubjectDao(subjectId: Int): List<PDF>

    @Insert
    fun insertPDF(vararg pdf: PDF)
}