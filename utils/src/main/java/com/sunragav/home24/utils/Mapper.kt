package com.sunragav.home24.utils

interface Mapper<E, T> {
    fun to(entity: E): T
    fun from(model: T): E
}