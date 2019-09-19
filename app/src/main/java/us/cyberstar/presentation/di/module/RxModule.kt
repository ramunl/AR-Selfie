package ru.pwtest.pwapp.di.module

import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

@Module
class RxModule {

    @Provides
    @us.cyberstar.common.IoScheduler
    fun provideSchedulerIO(): Scheduler = Schedulers.io()

    @Provides
    @us.cyberstar.common.ComputationScheduler
    fun provideSchedulerComputation(): Scheduler = Schedulers.computation()

    @Provides
    @us.cyberstar.common.MainScheduler
    fun provideSchedulerMainThread(): Scheduler = AndroidSchedulers.mainThread()

}
