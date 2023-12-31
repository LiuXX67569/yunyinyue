package com.example.neteasecloudmusic.api

object HttpConfig {
    private const val internet: String = "192.168.137.1"
    private const val port: String = "8080"
    const val sourceUrl: String = "http://${internet}:${port}/musicAppServer/"
    const val servlet: String = "http://${internet}:${port}/musicAppServer/"
}