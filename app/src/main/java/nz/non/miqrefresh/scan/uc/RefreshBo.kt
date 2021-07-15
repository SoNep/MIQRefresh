package nz.non.miqrefresh.scan.uc

data class RefreshBo(
    val arrivalDatesString: String,
    val receivedAt: String,
    val progress: Long
)