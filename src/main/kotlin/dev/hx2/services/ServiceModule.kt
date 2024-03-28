package dev.hx2.services

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


val serviceModule = module {
    singleOf(::Authentication)
}