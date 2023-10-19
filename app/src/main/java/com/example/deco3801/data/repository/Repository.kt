/**
 * Contains the abstract repository class for communicating with Firebase.
 */
package com.example.deco3801.data.repository

import android.util.Log
import com.example.deco3801.util.forEachApply
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

/**
 * Abstract repository class for communicating with Firebase.
 *
 * @param T The type of model being used.
 * @property clazz The class of the model being used.
 * @property listener The listener used to listen for changes in the database.
 * @constructor Creates a new Repository with the given class.
 */
abstract class Repository<T>(private val clazz: Class<T>) {
    private var listener: ListenerRegistration? = null

    /**
     * Gets all the models from the collection.
     *
     * @param subCollectionId The document id where the sub-collection is located, if required.
     * @return A list of all the models in the collection.
     *
     * @throws FirebaseFirestoreException Firestore exceptions that may occur when reading a document.
     */
    suspend fun getAll(subCollectionId: String? = null): List<T> {
        return getCollectionRef(subCollectionId)
            .get()
            .await()
            .toObjects(clazz)
    }

    /**
     * Gets a model by its document id.
     *
     * @param id The id of the document being retrieved.
     * @param subCollectionId The document id where the sub-collection is located, if required.
     * @return The model with the given id.
     *
     * @throws FirebaseFirestoreException Firestore exceptions that may occur when reading a document.
     */
    suspend fun getById(
        id: String,
        subCollectionId: String? = null,
    ): T? {
        return getCollectionRef(subCollectionId)
            .document(id)
            .get()
            .await()
            .toObject(clazz)
    }

    /**
     * Attach a listener to listen for changes on a specific document.
     *
     * @param id The id of the document being listened on.
     * @param subCollectionId The document id where the sub-collection is located, if required.
     * @param callback The callback function to be called when the document changes.
     */
    fun attachListenerById(
        id: String,
        subCollectionId: String? = null,
        callback: (T) -> Unit,
    ) {
        if (listener != null) {
            detachListener()
        }

        listener =
            getCollectionRef(subCollectionId).document(id).addSnapshotListener { snapshot, e ->
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

    /**
     * Attach a listener to listen for changes on a specific query.
     *
     * @param callback The callback function to be called when the query changes.
     * @param subCollectionId The document id where the sub-collection is located, if required.
     * @param queryBuilder The query builder function to be called when the query changes.
     */
    fun attachListenerWithQuery(
        callback: (List<T>) -> Unit,
        subCollectionId: String? = null,
        queryBuilder: (Query) -> Query,
    ) {
        if (listener != null) {
            detachListener()
        }
        val ref = getCollectionRef(subCollectionId)
        val query = queryBuilder(ref)

        listener =
            query.addSnapshotListener { querySnapshot, e ->
                if (e != null) {
                    Log.w("LISTENER", "Listen failed.", e)
                    return@addSnapshotListener
                }
                callback(querySnapshot!!.toObjects(clazz))
            }
    }

    /**
     * Detach the listener from the database.
     */
    fun detachListener() {
        listener?.remove()
        listener = null
    }

    /**
     * Delete a collection from the database.
     *
     * @param docId The document id where the sub-collection is located, if required.
     *
     * @throws FirebaseFirestoreException Firestore exceptions that may occur when deleting a document.
     */
    fun deleteCollection(docId: String? = null) {
        return getCollectionRef(docId).delete()
    }

    /**
     * Gets the collection reference used to communicate with the database.
     *
     * @param docId The document id where the sub-collection is located, if required.
     */
    abstract fun getCollectionRef(docId: String? = null): CollectionReference
}

/**
 * Deletes all the documents in a collection.
 *
 * @throws FirebaseFirestoreException Firestore exceptions that may occur when deleting a document.
 */
fun CollectionReference.delete() {
    this.get().addOnSuccessListener {
        it.documents.forEachApply {
            reference.delete()
        }
    }
}
