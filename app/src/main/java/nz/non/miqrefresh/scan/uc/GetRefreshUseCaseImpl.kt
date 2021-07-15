package nz.non.miqrefresh.scan.uc

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import nz.non.miqrefresh.scan.domain.RefreshService
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import javax.inject.Inject

class GetRefreshUseCaseImpl @Inject constructor(private val refreshService: RefreshService) :
    GetRefreshUseCase {

    private var isRefreshing = true

    override suspend fun startRefreshApi(): Flow<RefreshBo> {
        isRefreshing = true
        return scanApi()
    }

    override suspend fun startRefreshMiq(): Flow<RefreshBo> {
        isRefreshing = true
        return scanMiq().flowOn(Dispatchers.IO)
    }

    private suspend fun scanApi(): Flow<RefreshBo> {
        return flow {
            var progress = 0L
            while (isRefreshing) {
                progress += 1
                val result = refreshService.getSlots()
                emit(RefreshBo(result.arrivalDatesString, result.receivedAt, progress))
                delay(DELAY_API)
            }
        }
    }

    private suspend fun scanMiq(): Flow<RefreshBo> {
        return flow {
            var process = 0L
            while (isRefreshing) {
                process += 1
                runCatching {
                    Jsoup.connect("https://allocation.miq.govt.nz/portal/").get()
                }.onSuccess { doc: Document ->
                    getArrivalDates(doc)?.let {
                        emit(RefreshBo(it, System.currentTimeMillis().toString(), process))
                    }
                }.onFailure {
                    emit(RefreshBo("", it.toString(), process))
                }

                delay(DELAY_MIQ)
            }
        }
    }

    private fun getArrivalDates(doc: Document): String? {
        val calendar = doc.getElementById("accommodation-calendar-home")
        return calendar?.attr("data-arrival-dates")

    }

    override suspend fun stopRefresh(): Boolean {
        isRefreshing = false
        return true
    }

    companion object {
        private const val DELAY_API = 2000L
        private const val DELAY_MIQ = 7000L
    }
}
