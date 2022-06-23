package at.hagenberg.studex.proxy

import at.hagenberg.studex.core.Subject
import java.io.IOException
import java.util.*
import kotlin.jvm.Throws

interface ServiceProxy {

    @Throws(IOException::class)
    suspend fun postSubject(name: String)

    @Throws(IOException::class)
    suspend fun getSubjects(): MutableList<Subject>
}