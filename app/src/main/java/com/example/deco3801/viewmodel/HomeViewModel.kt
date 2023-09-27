package com.example.deco3801.viewmodel

import android.content.SharedPreferences
import android.location.Location
import android.util.Log
import androidx.core.content.edit
import com.example.deco3801.data.model.Art
import com.example.deco3801.data.model.User
import com.example.deco3801.data.repository.ArtRepository
import com.example.deco3801.data.repository.FollowRepository
import com.example.deco3801.ui.components.SnackbarManager
import com.example.deco3801.util.toGeoPoint
import com.example.deco3801.util.toLatLng
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import org.imperiumlabs.geofirestore.GeoFirestore
import org.imperiumlabs.geofirestore.GeoQuery
import org.imperiumlabs.geofirestore.listeners.GeoQueryDataEventListener
import java.util.Date
import javax.inject.Inject

data class HomeUiState(
    val currentLocation: LatLng? = null,
    val distanceInKm: Double = 10.0,
    val ready: Boolean = false,
)

sealed class ArtFilterAction {
    data class Following(val boolean: Boolean) : ArtFilterAction()
    data class FollowingUser(val user: User?) : ArtFilterAction()
    data class DateCreated(val date: Date?) : ArtFilterAction()
    data class Mine(val boolean: Boolean) : ArtFilterAction()
}

// https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/serializers.md#serializing-3rd-party-classes
object DateAsLongSerializer : KSerializer<Date> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Date", PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: Date) = encoder.encodeLong(value.time)
    override fun deserialize(decoder: Decoder): Date = Date(decoder.decodeLong())
}

@Serializable
data class ArtFilterState(
    var following: Boolean = false,
    @Serializable(with = DateAsLongSerializer::class)
    var dateCreated: Date? = null,
    var followingUser: User? = null,
    var mine: Boolean = false,
)


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val artRepo: ArtRepository,
    private val followRepo: FollowRepository,
) : AppViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    private val _filterState = MutableStateFlow(ArtFilterState())
    val filterState: StateFlow<ArtFilterState> = _filterState

    private val _filterAction = MutableStateFlow<ArtFilterAction?>(null)
    val filterAction: StateFlow<ArtFilterAction?> = _filterAction

    private val _activeArt = MutableStateFlow<MutableMap<String, Art>>(HashMap())
    val activeArt: StateFlow<MutableMap<String, Art>> = _activeArt

    private var _inactiveArt: MutableMap<String, Art> = HashMap()

    private var _geoQuery: GeoQuery? = null

    private val STORE_ART_FILTER = "artFilter"

    fun readFilterStateFromStore(store: SharedPreferences) {
        try {
            _filterState.value = Json.decodeFromString(store.getString(STORE_ART_FILTER, "")!!)
        } catch (e: Exception) {
            Log.d("STORE", e.toString())
            _filterState.value = ArtFilterState()
        }
    }

    fun updateFilterStateInStore(store: SharedPreferences) {
        store.edit(commit = true) {
            putString(STORE_ART_FILTER, Json.encodeToString(_filterState.value))
            Log.d("STORE", Json.encodeToString(_filterState.value))
        }
    }

    fun onFilterAction(action: ArtFilterAction?, store: SharedPreferences) {
        when (action) {
            is ArtFilterAction.DateCreated -> {
                _filterState.value = _filterState.value.copy(dateCreated = action.date)
            }

            is ArtFilterAction.Following -> {
                _filterState.value = _filterState.value.copy(following = action.boolean)
            }

            is ArtFilterAction.FollowingUser -> {
                _filterState.value = _filterState.value.copy(
                    following = action.user != null,
                    followingUser = action.user
                )
            }

            is ArtFilterAction.Mine -> {
                _filterState.value = _filterState.value.copy(mine = action.boolean)
            }

            else -> {
                _filterState.value = ArtFilterState()
            }
        }
        updateFilterStateInStore(store)
        launchCatching {
            val newActiveArt: MutableMap<String, Art> = hashMapOf()
            val newInactiveArt: MutableMap<String, Art> = hashMapOf()

            suspend fun swapArt(art: Art) =
                if (isArtInFilter(art))
                    newActiveArt.put(art.id, art)
                else
                    newInactiveArt.put(art.id, art)

            _activeArt.value.forEach { swapArt(it.value) }
            _inactiveArt.forEach { swapArt(it.value) }

            _inactiveArt = newInactiveArt
            _activeArt.value = newActiveArt
        }
    }

    fun onLocationChange(newValue: Location?) {
        if (newValue != null) {
            return onLocationChange(newValue.toLatLng())
        }
    }

    fun onLocationChange(newValue: LatLng) {
        _uiState.value = _uiState.value.copy(currentLocation = newValue)
        _geoQuery?.center = newValue.toGeoPoint()
    }

    fun onDistanceChange(newValue: Double) {
        _uiState.value = _uiState.value.copy(distanceInKm = newValue)
        _geoQuery?.radius = newValue
    }

    suspend fun isArtInFilter(art: Art): Boolean {
        // No filter
        if (_filterState.value == ArtFilterState()) {
            Log.d("FILTER", "HERE")
            return true
        }

        val isFollowing = _filterState.value.following && followRepo.isFollowing(art.userId) != null
        val isMine = _filterState.value.mine && art.userId == Firebase.auth.uid

        Log.d("FILTER", (isFollowing || isMine).toString())
        return isFollowing || isMine
    }

    fun listenForArt() {
        if (_uiState.value.currentLocation == null) {
            Log.w("GEOQUERY", "No location")
            return
        }

        _geoQuery = GeoFirestore(artRepo.getCollectionRef()).queryAtLocation(
            _uiState.value.currentLocation!!.toGeoPoint(),
            _uiState.value.distanceInKm
        )

        _geoQuery?.addGeoQueryDataEventListener(object : GeoQueryDataEventListener {
            // private var offset = 0.001

            override fun onDocumentEntered(documentSnapshot: DocumentSnapshot, location: GeoPoint) {
                val art = documentSnapshot.toObject<Art>() ?: return
                // DEBUG: Add offset since emulator is in a fixed location so all art is added at
                // the same spot and we only see one marker.
                // art.location = GeoPoint(art.location!!.latitude + offset, art.location!!.longitude)
                // offset += offset

                launchCatching {
                    if (!isArtInFilter(art)) {
                        // add to inactive
                        _inactiveArt.put(art.id, art)
                        return@launchCatching
                    }
                    _activeArt.value = _activeArt.value.toMutableMap().apply { put(art.id, art) }
                    Log.d("GEOQUERY", "ENTER $art")
                }

            }

            override fun onDocumentExited(documentSnapshot: DocumentSnapshot) {
                val art = documentSnapshot.toObject<Art>() ?: return
                // Remove from both inactive and active
                _inactiveArt.remove(art.id)
                _activeArt.value = _activeArt.value.toMutableMap().apply { remove(art.id) }
                Log.d("GEOQUERY", "EXIT $art")
            }

            override fun onDocumentMoved(documentSnapshot: DocumentSnapshot, location: GeoPoint) {
                val art = documentSnapshot.toObject<Art>() ?: return
                launchCatching {
                    if (!isArtInFilter(art)) {
                        // add to inactive
                        _inactiveArt.replace(art.id, art)
                        return@launchCatching
                    }
                    _activeArt.value =
                        _activeArt.value.toMutableMap().apply { replace(art.id, art) }
                    Log.d("GEOQUERY", "MOVED $art")
                }

            }

            override fun onDocumentChanged(documentSnapshot: DocumentSnapshot, location: GeoPoint) {
                // TODO: Client-side filtering
                val art = documentSnapshot.toObject<Art>() ?: return
                launchCatching {
                    if (!isArtInFilter(art)) {
                        // add to inactive
                        _inactiveArt.replace(art.id, art)
                        return@launchCatching
                    }
                    _activeArt.value =
                        _activeArt.value.toMutableMap().apply { replace(art.id, art) }
                    Log.d("GEOQUERY", "CHANGED $art")
                }

            }

            override fun onGeoQueryReady() {
                _uiState.value = _uiState.value.copy(ready = true)
            }

            override fun onGeoQueryError(exception: Exception) {
                SnackbarManager.showError(exception)
            }
        })
    }

    fun stopListen() {
        _geoQuery?.removeAllListeners()
    }
}