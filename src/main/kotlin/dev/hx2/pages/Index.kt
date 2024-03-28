package dev.hx2.pages

import dev.hx2.components.linkButton
import dev.hx2.models.ExposedGroupLike
import dev.hx2.models.ExposedUser
import kotlinx.html.*

fun HTML.index(auth: ExposedUser?, groups: List<ExposedGroupLike>) {
    println(groups)
    page("Hunter's Site", auth) {
        div {
            classes = setOf("flex", "flex-col", "flex-grow-1", "justify-center", "items-center", "gap-8", "text-center")
            if (auth == null) {
                linkButton("/register", "Register")
            } else {
                linkButton("/groups/new", "Create a Group")
                if (groups.isNotEmpty()) {
                    linkButton("/groups/invite", "Add Some Friends")
                    linkButton("/notes/new", "Create a Note")
                    linkButton("/notes/mine", "View My Notes")
                }
            }
        }
    }
}