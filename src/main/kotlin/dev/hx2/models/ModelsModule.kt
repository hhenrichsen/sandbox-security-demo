package dev.hx2.models

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val modelsModule = module {
    singleOf(::UserService)
    singleOf(::PostService)
}