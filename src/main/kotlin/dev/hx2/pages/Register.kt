package dev.hx2.pages

import dev.hx2.components.inputWithTitle
import kotlinx.html.*

fun HTML.register() {
    page("Register") {
        classes = setOf("flex", "flex-col", "flex-grow-1", "justify-center", "items-center")
        div {
            classes = setOf("bg-white", "@dark:bg-neutral-900", "p-4", "rounded-md", "shadow-md")
            h2 {
                classes = setOf("text-l", "font-semibold", "mb-4", "text-teal-500")
                +"Register"
            }

            form {
                attributes["hx-post"] = "/api/register"
                attributes["hx-ext"] = "json-enc"
                classes = setOf("flex", "flex-col")
                inputWithTitle("username", "Username")
                button {
                    classes = setOf("my-4", "p-2", "bg-teal-500", "text-white", "rounded-md")
                    +"Register"
                }
            }
        }
    }
}