package dev.hx2.pages

import dev.hx2.components.selectFromList
import dev.hx2.models.ExposedGroupLike
import dev.hx2.models.ExposedUser
import kotlinx.html.*

fun HTML.inviteUser(groups: List<ExposedGroupLike>, auth: ExposedUser) {
    page("Create Note", auth) {
        classes = setOf("flex", "flex-col", "flex-grow-1", "justify-center", "items-center")
        div {
            classes = setOf("bg-white", "@dark:bg-neutral-900", "p-4", "rounded-md", "shadow-md")
            h2 {
                classes = setOf("text-l", "font-semibold", "mb-4", "text-teal-500")
                +"Add to Group"
            }

            form {
                attributes["hx-ext"] = "json-enc"
                attributes["hx-post"] = "/api/groups/invite"
                classes = setOf("flex", "flex-col")
                label {
                    classes = setOf("text-xs", "font-light", "text-neutral-900", "@dark:text-neutral-100")
                    htmlFor = "group"
                    +"Group"
                }
                selectFromList("group", groups, "") { it.id.toString() to it.slug }
                label {
                    classes = setOf("text-xs", "font-light", "text-neutral-900", "@dark:text-neutral-100", "mt-4")
                    htmlFor = "username"
                    +"Username"
                }
                input {
                    id = "username"
                    name = "username"
                    type = InputType.text
                    classes = setOf(
                        "p-2",
                        "border",
                        "border-gray-300",
                        "rounded-md",
                        "focus:border-teal-500",
                        "active:border-teal-500",
                        "@dark:bg-neutral-800",
                        "border-neutral-300",
                        "@dark:border-neutral-700",
                        "max-w-full",
                        "@dark:text-neutral-100",
                    )
                }
                button {
                    classes = setOf("my-4", "p-2", "bg-teal-500", "text-white", "rounded-md")
                    +"Invite"
                }
            }

            div {
                id = "response"
            }
        }
    }
}

