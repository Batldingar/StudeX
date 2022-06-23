package at.hagenberg.studex.proxy

import at.hagenberg.studex.core.Subject
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

class ServiceProxyImpl : ServiceProxy {

    companion object {
        private val database = Firebase.database("https://studex-b9a51-default-rtdb.europe-west1.firebasedatabase.app")
        val subjectReference = database.getReference("subjects")
    }

    override suspend fun postSubject(name: String) {
        val subject = Subject(name)
        subjectReference.child(UUID.randomUUID().toString()).setValue(subject)
    }

    override suspend fun getSubjects(): MutableList<Subject> {
        var list = mutableListOf<Subject>()

        subjectReference.get().addOnSuccessListener {
            var subjects = it.value as Map<String, Object>
            subjects.entries.forEach { entry ->
                list.add(Subject((entry.value as HashMap<String, String>).values.toList()[0]))
            }
        }

        return list
    }
}