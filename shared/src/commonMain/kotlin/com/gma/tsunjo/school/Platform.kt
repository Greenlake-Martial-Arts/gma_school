package com.gma.tsunjo.school

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform