package net.abrudan.isntinstagram.model

import androidx.room.*
import com.google.android.gms.tasks.Task

@Dao
interface UserDao {
    @Query("SELECT * FROM User where uid=:userUid")
    suspend fun getUserData(userUid:String): Task<User>

    @Query("SELECT * FROM Post where uid=:userUid")
    suspend fun getUserPost(userUid:String): List<Post>

    @Delete
    suspend fun deleteMyUser(user:User)

    @Query("DELETE FROM Post")
    suspend fun deleteUserPost()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateMyUser(user:User): Long


}