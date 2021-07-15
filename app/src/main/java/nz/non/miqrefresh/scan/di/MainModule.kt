package nz.non.miqrefresh.scan.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import nz.non.miqrefresh.scan.domain.RefreshService
import nz.non.miqrefresh.scan.uc.GetRefreshUseCase
import nz.non.miqrefresh.scan.uc.GetRefreshUseCaseImpl
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(ViewModelComponent::class)
object MainModule {

    @Provides
    fun provideGetRefreshUseCase(refreshService: RefreshService): GetRefreshUseCase =
        GetRefreshUseCaseImpl(refreshService)

    @Provides
    fun provideRefreshService(): RefreshService =
        Retrofit.Builder()
            .baseUrl("https://miorefresh-github-actions-3fk5tzr5na-uc.a.run.app")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RefreshService::class.java)
}