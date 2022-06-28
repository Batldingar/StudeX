package at.hagenberg.studex.proxy

import android.content.Context
import androidx.room.Room

object DatabaseProxy {

    fun createProxy(context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "studex")
            .fallbackToDestructiveMigration()
            .build()
    }
}