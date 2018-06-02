package com.theevilroot.bashim.objects

import java.util.*

class ShowMoreQuote: IQuote {
    override fun id(): Int = 0

    override fun date(): Date = Date()

    override fun rating(): Int = 0

    override fun url(): String = ""

    override fun text(): String = ""

    override fun isShowMore(): Boolean = true
}