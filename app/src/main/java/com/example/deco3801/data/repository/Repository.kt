package com.example.deco3801.data.repository

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

abstract class Repository<T>(private val clazz: Class<T>) {
    private var listener: ListenerRegistration? = null

    suspend fun getAll(): List<T> {
        return getCollectionRef()
            .get()
            .await()
            .toObjects(clazz)
    }
    suspend fun getById(id: String): T? {
        return getCollectionRef()
            .document(id)
            .get()
            .await()
            .toObject(clazz)
    }

    fun attachListenerById(id: String, callback: (T) -> Unit) {
        if (listener != null) {
            detachListener()
        }

        listener = getCollectionRef().document(id).addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("LISTENER", "Listen failed.", e)
                return@addSnapshotListener
            }
            val obj = snapshot!!.toObject(clazz)
            if (obj == null) {
                Log.w("LISTENER", "Serilization failed.")
                return@addSnapshotListener
            }
            callback(obj)
        }
    }

    fun attachListenerWithQuery(callback: (List<T>) -> Unit, queryBuilder: (Query) -> Query) {
        if (listener != null) {
            detachListener()
        }
        val ref = getCollectionRef()
        val query = queryBuilder(ref)

        listener = query.addSnapshotListener { querySnapshot, e ->
            if (e != null) {
                Log.w("LISTENER", "Listen failed.", e)
                return@addSnapshotListener
            }
            callback(querySnapshot!!.toObjects(clazz))
        }
    }

    fun detachListener() {
        listener?.remove()
        listener = null
    }

    abstract fun getCollectionRef(): CollectionReference
}