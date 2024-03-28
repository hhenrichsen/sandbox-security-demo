package dev.hx2.components

import kotlinx.html.FlowOrInteractiveOrPhrasingContent
import kotlinx.html.InputType
import kotlinx.html.classes
import kotlinx.html.*

fun FlowOrInteractiveOrPhrasingContent.inputWithTitle(id: String, text: String) {
    label {
        classes = setOf("text-xs", "font-light", "text-neutral-900", "@dark:text-neutral-100")
        htmlFor = id
        +text
    }
    input {
        this.id = id
        name = id
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
            "@dark:text-neutral-100",
        )
    }
}
