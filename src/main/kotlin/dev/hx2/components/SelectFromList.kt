package dev.hx2.components


import kotlinx.html.*

fun <T> FlowOrInteractiveOrPhrasingContent.selectFromList(
    name: String,
    options: List<T>,
    choice: String,
    transformer: (value: T) -> Pair<String, String>,
) {
    select {
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
            "w-full",
            "text-md"
        )
        this.name = name
        options.forEach {
            val (v, option) = transformer(it)
            option {
                classes = setOf(
                    "p-1",
                    "text-neutral-900",
                    "@dark:text-neutral-100",
                    "bg-white",
                    "@dark:bg-neutral-900",
                    "rounded-md",
                    "active:bg-teal-500",
                    "active:text-white",
                    "hover:bg-teal-500",
                    "hover:text-white",
                )
                value = v
                +option
                if (choice == v) {
                    selected = true
                }
            }
        }
    }
}