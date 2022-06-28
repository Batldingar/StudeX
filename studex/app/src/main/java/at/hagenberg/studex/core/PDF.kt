package at.hagenberg.studex.core

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "pdfs")
data class PDF(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "pdf_id") val id: Int,
    @ColumnInfo(name = "document_name") val document_name: String,
    @ColumnInfo(name = "file_path") val file_path: String?,
    @ColumnInfo(name = "subject_id") val subject_id: Int
)