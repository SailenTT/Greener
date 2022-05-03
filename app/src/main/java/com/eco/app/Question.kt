package com.eco.app

data class Question(
    val question: String?,
    var listofrisp: ArrayList<String> = arrayListOf<String>()
) {
}