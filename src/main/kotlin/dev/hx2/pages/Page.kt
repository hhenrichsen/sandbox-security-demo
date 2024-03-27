package dev.hx2.pages

import dev.hx2.components.icon
import kotlinx.html.*


val iconColor = "10b981"
fun HTML.page(title: String, block: BODY.() -> Unit) {
    classes = setOf("h-full")
    head {
        title {
            +title
        }
        script(src = "https://cdn.jsdelivr.net/npm/@unocss/runtime") {}
        script(src = "https://unpkg.com/htmx.org@1.9.10") {}
        script(src = "https://cdn.jsdelivr.net/npm/alpinejs@3.x.x/dist/cdn.min.js") {
            defer = true
        }
        link(rel = "stylesheet", href = "https://cdn.jsdelivr.net/npm/@unocss/reset/normalize.min.css") {}
    }
    body {
        classes =
            setOf("h-full flex flex-col justify-center items-center @dark:bg-neutral-800 bg-neutral-100 m-0 gap-4")
        block()

        footer {
            classes =
                setOf("w-full flex justify-center items-center py-4 @dark:bg-neutral-900 bg-neutral-200 @dark:text-neutral-300 text-neutral-500 text-sm")
            span {
                classes = setOf("pr-2")
                +"Made with"
            }
            icon("material-symbols:favorite", color = iconColor)
            span {
                classes = setOf("pr-2")
                +","
            }
            a {
                href = "https://alpinejs.dev"
                target = ATarget.blank
                classes = setOf("flex justify-center items-center @dark:text-neutral-300 text-neutral-500")
                icon("simple-icons:alpinedotjs", color = iconColor)
            }
            span {
                classes = setOf("pr-2")
                +","
            }
            a {
                href = "https://htmx.org"
                target = ATarget.blank
                classes = setOf("flex justify-center items-center")
                icon("simple-icons:htmx", color = iconColor)
            }
            span {
                classes = setOf("px-2")
                +"and"
            }
            a {
                href = "https://ktor.io"
                target = ATarget.blank
                classes = setOf("flex justify-center items-center")
                icon("simple-icons:kotlin", color = iconColor)
            }

            span {
                classes = setOf("px-2")
                +"by Hunter."
            }
        }
    }
}