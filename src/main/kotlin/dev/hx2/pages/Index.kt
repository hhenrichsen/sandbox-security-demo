package dev.hx2.pages

import kotlinx.html.HTML
import kotlinx.html.classes
import kotlinx.html.div

fun HTML.index() {
    page("Hunter's Site") {
        div {
            classes = setOf("flex", "flex-col", "flex-grow-1", "justify-center", "items-center")
            div {
                classes = setOf("bg-white", "p-4", "rounded-md", "shadow-md")
                +"Hello from Hunter's Site"
            }
        }
    }
}