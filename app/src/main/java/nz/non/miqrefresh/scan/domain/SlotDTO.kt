package nz.non.miqrefresh.scan.domain

import com.google.gson.annotations.SerializedName

data class SlotDTO(
    @SerializedName("arrivalDatesString")
    val arrivalDatesString: String,
    @SerializedName("receivedAt")
    val receivedAt: String
)