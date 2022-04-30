package com.sonnt.moneymanagement.utils

import android.icu.text.NumberFormat

class NumberFormatter {
    companion object
    {
        private val nf = NumberFormat.getInstance()
        fun format(number : Long) : String = nf.format(number)
        fun format(number : Float) : String = nf.format(number)
        fun toLong(source : String) : Long = nf.parse(source).toLong()
    }
}