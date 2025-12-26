package com.gma.school.gma_school

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform