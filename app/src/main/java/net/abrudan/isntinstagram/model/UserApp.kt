package net.abrudan.isntinstagram.model

import android.app.Application
import androidx.room.Room

class UserApp : Application(){
    val room = Room.databaseBuilder(this,UserDatabase::class.java,"myUser").build()
}