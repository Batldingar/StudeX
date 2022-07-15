package at.hagenberg.studex

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import at.hagenberg.studex.core.Subject
import at.hagenberg.studex.proxy.AppDatabase
import at.hagenberg.studex.proxy.SubjectDao
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.After

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import java.io.IOException

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class SubjectTest {

    private lateinit var subjectDao: SubjectDao
    private lateinit var db: AppDatabase

    @Before
    fun testingSubjectDao() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        subjectDao = db.subjectDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(IOException::class)
    fun insertSubjectInDatabase() {
        subjectDao.insertSubject(Subject(0, "PRO"))
        subjectDao.insertSubject(Subject(0, "VIS"))
        subjectDao.insertSubject(Subject(0, "OIS"))
        subjectDao.insertSubject(Subject(0, "WIA"))
        subjectDao.insertSubject(Subject(0, "DAB"))

        val subjects = subjectDao.getAll()

        assertEquals(5, subjects.size)
        assertNotEquals(0, subjects[0].id)
    }
}