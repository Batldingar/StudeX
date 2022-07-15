package at.hagenberg.studex.proxy

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import at.hagenberg.studex.core.PDF
import at.hagenberg.studex.core.Subject
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDao {
    @Query("SELECT * FROM subjects")
    fun getAll(): List<Subject>

    @Query("SELECT * FROM subjects WHERE subjects.subject_id = :subjectId")
    fun getSubjectDetails(subjectId: Int): Subject

    @Insert
    fun insertSubject(vararg subject: Subject)

    @Delete
    fun delete(subject: Subject)
}