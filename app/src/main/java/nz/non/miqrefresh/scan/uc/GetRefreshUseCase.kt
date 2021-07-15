package nz.non.miqrefresh.scan.uc

import kotlinx.coroutines.flow.Flow

interface GetRefreshUseCase {
    suspend fun startRefreshApi(): Flow<RefreshBo>
    suspend fun startRefreshMiq(): Flow<RefreshBo>
    suspend fun stopRefresh(): Boolean
}