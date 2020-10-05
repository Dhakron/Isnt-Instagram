package net.abrudan.isntinstagram.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

private const val DATABASE = "homePost"

@Database(
    entities = [Post::class,User::class],
    version = 1,
    exportSchema = false
)
abstract class HomeDatabase : RoomDatabase() {

    abstract fun roomPostDao(): PostDao


    companion object {

        // For Singleton instantiation
        @Volatile
        private var instance: HomeDatabase? = null

        fun getInstance(context: Context): HomeDatabase {
            return instance ?: synchronized(this) {
                instance
                    ?: buildDatabase(context).also { instance }
            }
        }

        private fun buildDatabase(context: Context): HomeDatabase {
            return Room.databaseBuilder(context, HomeDatabase::class.java, DATABASE)
                .build()
        }
    }
}