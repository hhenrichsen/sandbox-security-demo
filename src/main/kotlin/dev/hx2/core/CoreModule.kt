package dev.hx2.core

import dev.hx2.core.impl.CommonmarkMarkdownService
import dev.hx2.core.impl.OwaspHtmlSanitizerService
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreModule = module {
    singleOf(::CommonmarkMarkdownService) bind MarkdownService::class
    singleOf(::OwaspHtmlSanitizerService) bind HtmlSanitizerService::class
}
