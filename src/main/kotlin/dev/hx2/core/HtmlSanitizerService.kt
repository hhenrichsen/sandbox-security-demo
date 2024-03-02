package dev.hx2.core

interface HtmlSanitizerService {
    fun sanitize(html: String): String
}