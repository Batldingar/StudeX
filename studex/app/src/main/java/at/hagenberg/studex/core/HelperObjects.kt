package at.hagenberg.studex.core

import androidx.room.ColumnInfo

data class SubjctDetails(
    private var id: Int,
    private var name: String?,
    private var pdfs: List<PDF>
)