public class Clock {
	
	//millisekunder for �n runde
	private float millisPerCycle;
	
	//tid for sidste opdatering
	private long lastUpdate;
	
	//Antal runder, som er g�et
	private int elapsedCycles;
	
	//M�ngden af overskydende tid indtil n�ste afsluttet runde
	private float excessCycles;
	
	//Om spillet er p� pause
	private boolean isPaused;
	
	public Clock(float cyclesPerSecond) {
		setCyclesPerSecond(cyclesPerSecond);
		reset();
	}
	
	public void setCyclesPerSecond(float cyclesPerSecond) {
		this.millisPerCycle = (1.0f / cyclesPerSecond) * 650;
	}
	
	public void reset() {
		this.elapsedCycles = 0;
		this.excessCycles = 0.0f;
		this.lastUpdate = getCurrentTime();
		this.isPaused = false;
	}
	
	public void update() {
		long currentUpdate = getCurrentTime();
		float delta = (float)(currentUpdate - lastUpdate) + excessCycles;
		
		if(!isPaused) {
			this.elapsedCycles += (int)Math.floor(delta / millisPerCycle);
			this.excessCycles = delta % millisPerCycle;
		}
		
		this.lastUpdate = currentUpdate;
	}
	
	public void setPaused(boolean paused) {
		this.isPaused = paused;
	}
	
	public boolean isPaused() {
		return isPaused;
	}
	
	public boolean hasElapsedCycle() {
		if (elapsedCycles > 0) {
			this.elapsedCycles--;
			return true;
		}
		return false;
	}
	
	public boolean peekElapsedCycle() {
		return (elapsedCycles > 0);
	}
	
	private static final long getCurrentTime() {
		return (System.nanoTime() / 1000000L);
	}
}
