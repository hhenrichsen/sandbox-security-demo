package dev.hx2.pages

import dev.hx2.components.icon
import dev.hx2.models.ExposedUser
import kotlinx.html.*


const val iconColor = "10b981"
fun HTML.page(title: String, user: ExposedUser? = null, block: MAIN.() -> Unit) {
    classes = setOf("min-h-full", "h-full", "flex", "flex-col")
    head {
        title {
            +title
        }
        script(src = "https://cdn.jsdelivr.net/npm/@unocss/runtime") {}
        script(src = "https://unpkg.com/htmx.org@1.9.10") {}
        script(src = "https://unpkg.com/htmx.org@1.9.11/dist/ext/json-enc.js") {}
        script(src = "https://cdn.jsdelivr.net/npm/alpinejs@3.x.x/dist/cdn.min.js") {
            defer = true
        }
        link(rel = "stylesheet", href = "https://cdn.jsdelivr.net/npm/@unocss/reset/normalize.min.css") {}
    }
    body {
        classes =
            setOf(
                "min-h-full",
                "flex",
                "flex-grow-1",
                "flex-col",
                "justify-center",
                "items-center",
                "@dark:bg-neutral-800",
                "bg-neutral-100",
                "m-0",
                "gap-4",
                "font-sans"
            )
        header {
            classes =
                setOf(
                    "w-full",
                    "flex",
                    "justify-center",
                    "items-center",
                    "flex-col",
                    "py-4",
                    "@dark:bg-neutral-900",
                    "bg-neutral-200",
                    "@dark:text-neutral-300",
                    "text-neutral-500",
                    "text-sm",
                    "gap-2"
                )
            a {
                href = "/"
                classes = setOf(
                    "flex",
                    "justify-center",
                    "items-center",
                    "text-teal-500",
                    "hover:text-teal-600",
                    "@dark:hover:text-teal-400",
                    "transition-colors",
                    "text-lg",
                    "font-bold",
                    "decoration-none"
                )
                +"Pass a Note"
            }
            if (user != null) {
                span {
                    +"Logged in as ${user.username}"
                }
            }
        }

        main {
            classes = setOf("flex-grow-1", "min-h-full", "flex", "flex-col", "justify-center", "items-center")
            block()
        }

        footer {
            classes =
                setOf(
                    "w-full",
                    "flex",
                    "justify-center",
                    "items-center",
                    "py-4",
                    "@dark:bg-neutral-900",
                    "bg-neutral-200",
                    "@dark:text-neutral-300",
                    "text-neutral-500",
                    "text-sm",
                    "gap-8"
                )
            div {
                classes = setOf("flex", "justify-center", "items-center")
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
                    classes =
                        setOf("flex", "justify-center", "items-center", "@dark:text-neutral-300", "text-neutral-500")
                    icon("simple-icons:alpinedotjs", color = iconColor)
                }
                span {
                    classes = setOf("pr-2")
                    +","
                }
                a {
                    href = "https://htmx.org"
                    target = ATarget.blank
                    classes = setOf("flex", "justify-center", "items-center")
                    icon("simple-icons:htmx", color = iconColor)
                }
                span {
                    classes = setOf("px-2")
                    +"and"
                }
                a {
                    href = "https://ktor.io"
                    target = ATarget.blank
                    classes = setOf("flex", "justify-center", "items-center")
                    icon("devicon-plain:ktor", color = iconColor)
                }

                span {
                    classes = setOf("px-2")
                    +"by Hunter in Salt Lake, Utah."
                }
            }
            div {
                classes = setOf("flex", "justify-center", "items-center", "flex-row")
                span {
                    attributes["x-cloak"] = ""
                    attributes["x-init"] = "fetch('/api/health').then(res => { ok = res.ok })"
                    attributes["x-data"] = "{ ok: undefined } "
                    classes = setOf("pr-2")
                    +"Server Status: "

                    span {
                        attributes["x-show"] = "ok === true"
                        classes = setOf("text-teal-500")
                        +"OK"
                    }

                    span {
                        attributes["x-show"] = "ok === undefined"
                        +"Checking..."
                    }

                    span {
                        attributes["x-show"] = "ok === false"
                        classes = setOf("text-red-500")
                        +"Down"
                    }
                }
            }
        }
    }
}