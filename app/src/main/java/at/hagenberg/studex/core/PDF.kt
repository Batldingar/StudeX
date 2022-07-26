package at.hagenberg.studex.core

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "pdfs", primaryKeys = ["document_name", "subject_id", "stage"])
data class PDF(
    @ColumnInfo(name = "persistent_name") val persistent_name: String,
    @ColumnInfo(name = "document_name") val document_name: String,
    @ColumnInfo(name = "subject_id") val subject_id: Int,
    @ColumnInfo(name = "stage") val stage: Int
)