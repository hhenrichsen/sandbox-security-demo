package dev.hx2.components

import kotlinx.html.*

fun FlowOrInteractiveOrPhrasingContent.linkButton(link: String, text: String) {
    a {
        classes = setOf(
            "text-l",
            "font-semibold",
            "bg-teal-500",
            "text-neutral-100",
            "p-8",
            "rounded-md",
            "shadow-md",
            "decoration-none",
            "hover:bg-teal-600",
            "@dark:hover:bg-teal-400",
            "min-w-36",
        )
        href = link
        div {
            +text
        }
    }
}
