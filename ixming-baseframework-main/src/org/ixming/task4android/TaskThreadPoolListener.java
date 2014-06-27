package org.ixming.task4android;

interface TaskThreadPoolListener {

	void beforeExecute(Thread t, Runnable r) ;
	
	void afterExecute(Runnable r, Throwable t) ;
	
	void terminated() ;
	
}
