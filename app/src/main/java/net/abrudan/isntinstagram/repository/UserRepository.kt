package net.abrudan.isntinstagram.repository

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.HttpsCallableResult

class UserRepository {
    val user= FirebaseAuth.getInstance()
    val functions = FirebaseFunctions.getInstance(FirebaseApp.getInstance(),"europe-west1")


    //function set UserID
    fun setUserID(userID: String): Task<HttpsCallableResult> {
        val data = hashMapOf(
            "userID" to userID,
            "push" to true
        )
        return functions.getHttpsCallable("setUserID")
            .call(data)
            .continueWith { task ->
                return@continueWith task.result
            }
    }

}