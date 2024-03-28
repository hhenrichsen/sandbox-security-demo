package dev.hx2.pages

import dev.hx2.models.ExposedGroup
import dev.hx2.models.ExposedNote
import dev.hx2.models.ExposedUser
import kotlinx.html.*

fun HTML.postView(auth: ExposedUser?, note: ExposedNote, group: ExposedGroup, content: String) {
    page(note.title, auth) {
        main {
            println(group.members)
            println(auth?.id)
            println(note)
            attributes["x-init"] =
                "${note.public || group.members.any { it.id == auth?.id }} ? null :  window.location.href = '/' "
            classes = setOf("bg-white", "@dark:bg-neutral-900", "p-4", "rounded-md", "shadow-md", "min-w-[65ch]")
            h2 {
                classes = setOf("text-3xl", "font-bold", "mb-4")
                +"${note.title} (via ${note.owner.title})"
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
                                
                    @media (prefers-color-scheme: dark) {
                        body {
                            color: #f7fafc;
                            background-color: #1a202c;
                        }
                        
                        h1, h2, h3, h4, h5, h6 {
                            color: #f7fafc;
                        }
                        
                        p {
                            color: #cbd5e0;
                        }
                        
                        a {
                            color: #63b3ed;
                        }
                        
                        a:hover {
                            color: #4299e1;
                        }
                        
                        blockquote {
                            color: #cbd5e0;
                            border-left-color: #cbd5e0;
                        }
                        
                        pre {
                            color: #cbd5e0;
                            background-color: #2d3748;
                        }
                        
                        code {
                            color: #cbd5e0;
                            background-color: #2d3748;
                        }
                        
                        ol > li {
                            color: #cbd5e0;
                        }
                        
                        ul > li {
                            color: #cbd5e0;
                        }
                    }
                    
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
                    
                    * {
                        max-width: 100%;
                    }
                """.trimIndent()
                    }
                }
                unsafe {
                    +content
                }
            }
        }
    }
}