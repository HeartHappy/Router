package com.hearthappy.router.ext

fun String.rePathName(): String {
    val replaceFirstChar = this.split("/").joinToString("") { it.replaceFirstChar { rfc -> rfc.uppercaseChar() } }.replaceFirstChar { it.uppercase() }
    return "Path$$".plus(replaceFirstChar)
}

fun String.reRouterName(): String {
    return "Router$$${this.substringAfterLast(".")}"
}