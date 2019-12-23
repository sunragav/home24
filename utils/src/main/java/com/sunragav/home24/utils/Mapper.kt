package com.sunragav.home24.utils

interface Mapper<T, E> {
    fun to(t: T): E
    fun from(e: E): T
}