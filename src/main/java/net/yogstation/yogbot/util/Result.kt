package net.yogstation.yogbot.util

class Result<T, E> private constructor(val value: T?, val error: E?) {

	fun hasError(): Boolean {
		return error != null
	}

	fun hasValue(): Boolean {
		return value != null
	}

	companion object {

		fun <T, E> success(success: T): Result<T, E?> {
			return Result(success, null)
		}

		fun <T, E> error(error: E): Result<T?, E> {
			return Result(null, error)
		}
	}
}
