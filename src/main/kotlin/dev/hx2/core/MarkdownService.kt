package dev.hx2.core

interface MarkdownService {
    fun parse(markdown: String): String
}