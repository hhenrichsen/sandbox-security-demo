package dev.hx2.core.impl

import dev.hx2.core.MarkdownService
import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser

class CommonmarkMarkdownService : MarkdownService {
    private val flavorDescriptor = CommonMarkFlavourDescriptor()
    private val md: MarkdownParser = MarkdownParser(flavorDescriptor)

    override fun parse(markdown: String): String {
        val tree = this.md.buildMarkdownTreeFromString(markdown)
        val generator = HtmlGenerator(markdown, tree, flavorDescriptor, false)
        return generator.generateHtml();
    }
}