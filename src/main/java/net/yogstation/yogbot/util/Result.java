package net.yogstation.yogbot.util;

public class Result<T, E> {
	private final T value;
	private final E error;

	private Result(T success, E error) {
		this.value = success;
		this.error = error;
	}

	public static<T, E> Result<T, E> success(T success) {
		return new Result<>(success, null);
	}

	public static<T, E> Result<T, E> error(E error) {
		return new Result<>(null, error);
	}

	public boolean hasError() {
		return error != null;
	}

	public T getValue() {
		return value;
	}

	public E getError() {
		return error;
	}
}
