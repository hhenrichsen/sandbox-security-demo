package dev.hx2.pages

import dev.hx2.models.ExposedNote
import dev.hx2.models.ExposedUser
import kotlinx.html.*
import java.lang.Integer.min

fun HTML.myNotes(notes: List<ExposedNote>, auth: ExposedUser) {
    page("My Notes", auth) {
        classes = setOf("flex", "flex-col", "flex-grow-1", "justify-center", "items-center")
        notes.forEach {
            a {
                classes = setOf(
                    "decoration-none",
                    "flex",
                    "flex-col",
                    "w-prose",
                    "flex-shrink-1"
                )
                href = "/notes/${it.owner.slug}/${it.id}"
                article {
                    classes = setOf(
                        "bg-white",
                        "@dark:bg-neutral-900",
                        "p-4",
                        "rounded-md",
                        "shadow-md",
                        "mb-4",
                    )
                    h2 {
                        classes = setOf("text-l", "font-semibold", "mb-4", "text-teal-500")
                        +"${it.title} (via ${it.owner.title})"
                    }
                    p {
                        classes = setOf(
                            "text-sm",
                            "text-neutral-900",
                            "@dark:text-neutral-100",
                            "max-w-prose",
                        )
                        +it.content.slice(0..min(100, it.content.length - 1))
                    }
                }
            }
        }
    }
}