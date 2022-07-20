package at.hagenberg.studex.proxy

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import at.hagenberg.studex.core.Question

@Dao
interface QuestionDao {

    @Query("SELECT * FROM questions WHERE questions.pdf_id = :pdfID")
    fun getQuestionsForPdf(pdfID: Int): List<Question>

    @Insert
    fun insertQuestion(question: Question)
}

