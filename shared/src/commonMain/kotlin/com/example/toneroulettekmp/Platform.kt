package com.example.toneroulettekmp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform