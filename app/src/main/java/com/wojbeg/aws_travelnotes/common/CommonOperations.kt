package com.wojbeg.aws_travelnotes.common

fun cleanText(s: String): String {
    return s.replace("\n", "").filter { !it.isWhitespace() }
}