package dev.hx2.core.impl

import dev.hx2.core.HtmlSanitizerService
import org.owasp.html.Sanitizers

class OwaspHtmlSanitizerService : HtmlSanitizerService {
    private val policy =
        Sanitizers.BLOCKS
            .and(Sanitizers.FORMATTING)
            .and(Sanitizers.IMAGES)
            .and(Sanitizers.LINKS)
            .and(Sanitizers.TABLES)

    override fun sanitize(html: String): String {
        return policy.sanitize(html)
    }
}