package dev.hx2.pages

import kotlinx.html.*

fun HTML.page(title: String, block: BODY.() -> Unit) {
    classes = setOf("bg-gray-100", "h-full")
    head {
        title { +title }
        script(src = "https://cdn.tailwindcss.com?plugins=forms,typography,aspect-ratio,line-clamp") {}
        script(src = "https://unpkg.com/htmx.org@1.9.10") {}
        script(src = "https://cdn.jsdelivr.net/npm/alpinejs@3.x.x/dist/cdn.min.js") {
            defer = true
        }
    }
    body {
        classes = setOf("h-full flex flex-col justify-center items-center")
        block()
    }
}