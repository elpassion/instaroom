package pl.elpassion.instaroom

import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import pl.elpassion.instaroom.api.InstaRoomApi
import pl.elpassion.instaroom.api.RetrofitInstaRoomApi
import pl.elpassion.instaroom.dashboard.DashboardViewModel
import pl.elpassion.instaroom.login.LoginRepository
import pl.elpassion.instaroom.login.LoginRepositoryImpl
import pl.elpassion.instaroom.login.LoginViewModel

val appModule = module {

    single<LoginRepository> { LoginRepositoryImpl(androidApplication()) }
    single<InstaRoomApi> { RetrofitInstaRoomApi }

    viewModel { LoginViewModel(get()) }
    viewModel { DashboardViewModel(get(), get()) }
}