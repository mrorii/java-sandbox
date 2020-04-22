package com.mrorii.javasandbox.fastutil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
public class FastUtilBenchmark {

	@Param({"100", "1000", "10000", "100000"})
	public long mapSize;

	@Benchmark
	public Long2ObjectMap givenFastUtilsMapWithInitialSizeSet_whenPopulated_checkTimeTaken() {
		Long2ObjectMap<String> map = new Long2ObjectOpenHashMap<>((int) mapSize);
		for (long i = 0; i < mapSize; i++) {
			map.put(i, String.valueOf(i));
		}
		for (long i = 0; i < mapSize; i++) {
			map.put(i, map.get(i) + map.get(i));
		}
		return map;
	}

	@Benchmark
	public Map<Long, String> givenFastUtilsMap2WithInitialSizeSet_whenPopulated_checkTimeTaken() {
		Map<Long, String> map = new Long2ObjectOpenHashMap<>((int) mapSize);
		for (long i = 0; i < mapSize; i++) {
			map.put(i, String.valueOf(i));
		}
		for (long i = 0; i < mapSize; i++) {
			map.put(i, map.get(i) + map.get(i));
		}
		return map;
	}

	@Benchmark
	public Map<Long, String> givenCollectionsMapWithInitialSizeSet_whenPopulated_checkTimeTaken() {
		Map<Long, String> map = new HashMap<>((int) mapSize);
		for (long i = 0; i < mapSize; i++){
			map.put(i, String.valueOf(i));
		}
		for (long i = 0; i < mapSize; i++){
			map.put(i, map.get(i) + map.get(i));
		}
		return map;
	}

	public static void main(String... args) throws RunnerException {
		Options opts = new OptionsBuilder()
				.include(".*")
				.warmupIterations(1)
				.measurementIterations(2)
				.jvmArgs("-Xms2g", "-Xmx2g")
				.shouldDoGC(true)
				.forks(1)
				.build();

		new Runner(opts).run();
	}
}
