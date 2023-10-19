package com.goofygoobers.geoart.directions.data.repository

import com.goofygoobers.geoart.directions.data.remote.GooglePlacesApi
import com.goofygoobers.geoart.directions.domain.model.GooglePlacesInfo
import com.goofygoobers.geoart.directions.domain.repository.GooglePlacesInfoRepository
import com.goofygoobers.geoart.directions.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

// Directions was created using below
// Kadhi Chienja, "JetMapCompose", 16 October 2023. [Online]. Available: https://github.com/kahdichienja/JetMapCompose

// Calls the Google api and stores the result
class GooglePlacesInfoRepositoryImplementation(private val api: GooglePlacesApi) :
    GooglePlacesInfoRepository {
    override fun getDirection(
        origin: String,
        destination: String,
        key: String,
    ): Flow<Resource<GooglePlacesInfo>> =
        flow {
            emit(Resource.Loading())
            try {
                val directionData =
                    api.getDirection(
                        origin = origin,
                        destination = destination,
                        key = key,
                    )
                emit(Resource.Success(data = directionData))
            } catch (e: HttpException) {
                emit(Resource.Error(message = "Oops something is not right: $e"))
            } catch (e: IOException) {
                emit(Resource.Error(message = "No Internet Connection: $e"))
            }
        }
}
