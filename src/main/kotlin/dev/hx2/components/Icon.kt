package dev.hx2.components

import kotlinx.html.FlowOrInteractiveOrPhrasingContent
import kotlinx.html.classes
import kotlinx.html.img


fun FlowOrInteractiveOrPhrasingContent.icon(name: String, color: String = "232323", classes: Set<String> = setOf()) {
    img {
        this.classes = setOf(*classes.toTypedArray(), "w-4", "h-4")
        src = "https://api.iconify.design/${name}.svg?color=%23${color}"
    }
}