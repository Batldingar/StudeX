package at.hagenberg.studex.core

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subjects")
data class Subject(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "subject_id") val id: Int,
    @ColumnInfo(name = "name") val name: String?,
)
