package at.hagenberg.studex.core

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import com.google.type.DateTime

@Entity(tableName = "questions", foreignKeys = arrayOf(ForeignKey(entity = PDF::class, parentColumns = arrayOf("pdf_id"), childColumns = arrayOf("question_id"), onUpdate = CASCADE, onDelete = CASCADE)))
data class Question(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "question_id") val id: Int,
    @ColumnInfo(name = "question") val question: String,
    @ColumnInfo(name = "answer") val answer: String,
    @ColumnInfo(name = "difficulty") val difficulty: Int,
    @ColumnInfo(name = "pdf_id") val pdfId: Int?
)