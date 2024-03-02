package dev.hx2.pages

import kotlinx.html.*

fun HTML.postView(title: String, body: String) {
    page(title) {
        div {
            classes = setOf("min-h-full flex flex-col justify-center items-center")
            main {
                classes = setOf("bg-white", "p-4", "rounded-md", "shadow-md", "min-w-[65ch]")
                h2 {
                    classes = setOf("text-3xl", "font-bold", "mb-4")
                    +title
                }

                div {
                    classes = setOf("border-b-2", "border-gray-200", "mb-4", "w-full")
                }

                div {
                    classes = setOf("max-w-prose min-w-content")
                    // TODO: This is gross, but we can fix it later. It works to override tailwind's default styles.
                    style {
                        unsafe {
                            +"""
                    p {
                         margin-top: 1rem;
                    }
                    
                    ul > li {
                        list-style-type: disc; 
                        margin-left: 1rem;
                    }
                    
                    ol > li {
                        list-style-type: decimal;
                        margin-left: 1rem;
                    }
                    
                    a {
                        color: #3182ce;
                        text-decoration: underline;
                    }
                    
                    a:hover {
                        color: #2c5282;
                    }
                    
                    h2 {
                        font-size: 1.5rem;
                        font-weight: 600;
                        line-height: 2rem;
                        margin-top: 1.5rem;
                        margin-bottom: 0.5rem;
                    }
                """.trimIndent()
                        }
                    }
                    unsafe {
                        +body
                    }
                }
            }
        }
    }
}