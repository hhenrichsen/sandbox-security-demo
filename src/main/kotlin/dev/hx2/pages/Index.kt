package dev.hx2.pages

import kotlinx.html.HTML
import kotlinx.html.classes
import kotlinx.html.div

fun HTML.index() {
    page("Hunter's Site") {
        div {
            classes = setOf("min-h-full flex flex-col justify-center items-center")
            div {
                classes = setOf("bg-white", "p-4", "rounded-md", "shadow-md")
                +"Hello from Hunter's Site"
            }
        }
    }
}