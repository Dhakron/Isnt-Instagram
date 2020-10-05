package net.abrudan.isntinstagram.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

private const val DATABASE = "myUser"

@Database(
    entities = [Post::class,User::class],
    version = 1,
    exportSchema = false
)
abstract class UserDatabase : RoomDatabase() {

    abstract fun roomUserDao(): UserDao


    companion object {

        // For Singleton instantiation
        @Volatile
        private var instance: UserDatabase? = null

        fun getInstance(context: Context): UserDatabase {
            return instance ?: synchronized(this) {
                instance
                    ?: buildDatabase(context).also { instance }
            }
        }

        private fun buildDatabase(context: Context): UserDatabase {
            return Room.databaseBuilder(context, UserDatabase::class.java, DATABASE)
                .build()
        }
    }
}