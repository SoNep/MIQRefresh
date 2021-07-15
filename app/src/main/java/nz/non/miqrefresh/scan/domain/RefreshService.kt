package nz.non.miqrefresh.scan.domain

import retrofit2.http.GET

interface RefreshService {
    @GET("miorefresh")
    suspend fun getSlots(): SlotDTO
}

