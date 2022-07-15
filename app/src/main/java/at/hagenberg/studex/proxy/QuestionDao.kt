package at.hagenberg.studex.proxy

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import at.hagenberg.studex.core.Question
import at.hagenberg.studex.core.QuestionsOrderedByPDF

@Dao
interface QuestionDao {

    @Query("SELECT * FROM questions WHERE questions.pdf_id = :pdfId")
    fun getQuestionsForPdf(pdfId: Int): List<Question>

    @Insert
    fun insertQuestion(question: Question)
}

