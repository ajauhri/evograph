package utils;

public class Clock {
	long init;
	
	public Clock() {}
	
	public void init() {
		init = System.currentTimeMillis();
	}
	
	public long diff() {
		return System.currentTimeMillis() - init;
	}
	
	public void benchmark(String task) {
		long diff = diff();
		System.out.println(task + " took " + diff);
		init();
	}
}
