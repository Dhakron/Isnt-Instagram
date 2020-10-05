package net.abrudan.isntinstagram.model

import androidx.room.*

@Dao
interface PostDao {
    @Query("SELECT * FROM Post")
    suspend fun getHomePosts(): List<Post>

    @Query("DELETE FROM Post")
    suspend fun deleteAllPost()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePost(postList:List<Post>): Long
}