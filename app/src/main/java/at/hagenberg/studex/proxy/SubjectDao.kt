package at.hagenberg.studex.proxy

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import at.hagenberg.studex.core.Subject

@Dao
interface SubjectDao {
    @Query("SELECT * FROM subjects")
    fun getAll(): List<Subject>

    @Query("SELECT * FROM subjects WHERE subjects.subject_id = :subjectID")
    fun getSubjectDetails(subjectID: Int): Subject

    @Insert
    fun insertSubject(vararg subject: Subject)

    @Delete
    fun delete(subject: Subject)
}