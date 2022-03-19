package net.yogstation.yogbot.util;

import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class MonoCollector implements Collector<Mono<?>, List<Mono<?>>, Mono<?>> {

	public static MonoCollector toMono() {
		return new MonoCollector();
	}

	@Override
	public Supplier<List<Mono<?>>> supplier() {
		return ArrayList::new;
	}

	@Override
	public BiConsumer<List<Mono<?>>, Mono<?>> accumulator() {
		return List::add;
	}

	@Override
	public BinaryOperator<List<Mono<?>>> combiner() {
		return (list1, list2) -> {
			list1.addAll(list2);
			return list1;
		};
	}

	@Override
	public Function<List<Mono<?>>, Mono<?>> finisher() {
		return list -> {
			Mono<?> result = Mono.empty();
			for(Mono<?> mono : list) {
				result = result.and(mono);
			}
			return result;
		};
	}

	@Override
	public Set<Characteristics> characteristics() {
		return Set.of(Characteristics.UNORDERED);
	}
}
