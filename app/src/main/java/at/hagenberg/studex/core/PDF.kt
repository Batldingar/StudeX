package at.hagenberg.studex.core

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pdfs")
data class PDF(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "pdf_id") val id: Int,
    @ColumnInfo(name = "document_name") val document_name: String,
    @ColumnInfo(name = "persistent_name") val persistent_name: String,
    @ColumnInfo(name = "subject_id") val subject_id: Int
    // TODO: Add stage
)