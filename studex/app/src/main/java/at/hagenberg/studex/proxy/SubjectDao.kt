package at.hagenberg.studex.proxy

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import at.hagenberg.studex.core.Subject

@Dao
interface SubjectDao {
    @Query("SELECT * FROM subjects")
    fun getAll(): List<Subject>

    @Insert
    fun insertSubject(vararg subject: Subject)
}