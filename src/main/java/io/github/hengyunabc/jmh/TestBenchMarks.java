package io.github.hengyunabc.jmh;

import java.util.Date;
import java.util.Random;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

public class TestBenchMarks {
	public enum ChannelState {
		CONNECTED, DISCONNECTED, SENT, RECEIVED, CAUGHT
	}

	@State(Scope.Benchmark)
	public static class ExecutionPlan {
		@Param({ "1000000" })
		public int size;
		public ChannelState[] states = null;

		@Setup
		public void setUp() {
			ChannelState[] values = ChannelState.values();
			states = new ChannelState[size];
			Random random = new Random(new Date().getTime());
			for (int i = 0; i < size; i++) {
				int nextInt = random.nextInt(1000000);
				if (nextInt > 1) {
					states[i] = ChannelState.RECEIVED;
				} else {
					states[i] = values[nextInt % values.length];
				}
			}
		}
	}

	@Fork(value = 5)
	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	public void benchSiwtch(ExecutionPlan plan, Blackhole bh) {
		int result = 0;
		for (int i = 0; i < plan.size; ++i) {
			switch (plan.states[i]) {
			case CONNECTED:
				result += ChannelState.CONNECTED.ordinal();
			case DISCONNECTED:
				result += ChannelState.DISCONNECTED.ordinal();
			case SENT:
				result += ChannelState.SENT.ordinal();
			case RECEIVED:
				result += ChannelState.RECEIVED.ordinal();
			case CAUGHT:
				result += ChannelState.CAUGHT.ordinal();
			}
		}
		bh.consume(result);
	}

	@Fork(value = 5)
	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	public void benchIfAndSwitch(ExecutionPlan plan, Blackhole bh) {
		int result = 0;
		for (int i = 0; i < plan.size; ++i) {
			ChannelState state = plan.states[i];
			if (state == ChannelState.RECEIVED) {
				result += ChannelState.RECEIVED.ordinal();
			} else {
				switch (state) {
				case CONNECTED:
					result += ChannelState.CONNECTED.ordinal();
				case SENT:
					result += ChannelState.SENT.ordinal();
				case DISCONNECTED:
					result += ChannelState.DISCONNECTED.ordinal();
				case CAUGHT:
					result += ChannelState.CAUGHT.ordinal();
				}
			}
		}
		bh.consume(result);
	}
}
