package com.example.deco3801.directions.data.repository

import com.example.deco3801.directions.domain.model.GooglePlacesInfo
import com.example.deco3801.directions.domain.repository.GooglePlacesInfoRepository
import com.example.deco3801.directions.util.Resource
import com.example.deco3801.directions.data.remote.GooglePlacesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

// Directions was created using below
// Kadhi Chienja, "JetMapCompose", 16 October 2023. [Online]. Available: https://github.com/kahdichienja/JetMapCompose
class GooglePlacesInfoRepositoryImplementation(private val api: GooglePlacesApi):
    GooglePlacesInfoRepository {
    override fun getDirection(
        origin: String,
        destination: String,
        key: String
    ): Flow<Resource<GooglePlacesInfo>> = flow{
        emit(Resource.Loading())
        try {
            val directionData = api.getDirection(origin = origin, destination = destination, key=key)
            emit(Resource.Success(data = directionData))
        }catch (e: HttpException){
            emit(Resource.Error(message = "Oops something is not right: $e"))
        }catch (e: IOException){
            emit(Resource.Error(message = "No Internet Connection: $e"))
        }
    }

}
