package com.spire.posmate.utils;

import android.util.Log;

import com.spire.posmate.Logger;

import java.util.concurrent.Semaphore;

public class BoundedSemaphore extends Semaphore {
	private static final long serialVersionUID = 1L;
	
	private int permits;
	private final int maxPermits;
	
	public BoundedSemaphore(int permits) {
		super(permits);
		
		this.permits = permits;
		this.maxPermits = permits;
	}
	
	@Override
	public void acquire() throws InterruptedException {
		synchronized (this) {
			permits--;
			Log.d(Logger.LOG_TAG, "BoundedSemaphore: Acquiring permit: " + permits);
		}
		super.acquire();
	}

	@Override
	public synchronized void release() {
		super.release();
		
		permits++;
		Log.d(Logger.LOG_TAG, "BoundedSemaphore: Permit released: " + permits);
		
		if (permits > maxPermits) {
			Log.d(Logger.LOG_TAG, "BoundedSemaphore: Correcting permit count to: " + maxPermits);
			
			reducePermits(permits - maxPermits);
			permits = maxPermits;
		}
	}
}
