package at.hagenberg.studex.proxy

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import at.hagenberg.studex.core.PDF
import at.hagenberg.studex.core.Question
import at.hagenberg.studex.core.Subject

@Database(entities = [Subject::class, Question::class, PDF::class], version = 2)
abstract class AppDatabase: RoomDatabase() {

    abstract fun subjectDao(): SubjectDao
    abstract fun pdfDao(): PDFDao
    abstract fun questionDao(): QuestionDao
}