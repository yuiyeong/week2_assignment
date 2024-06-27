package com.yuiyeong.lectureenroll.controller.dto

data class ErrorResult(val message: String)

open class Result<T>(open val data: T)

class ListResult<T>(list: List<T>) : Result<List<T>>(list)
