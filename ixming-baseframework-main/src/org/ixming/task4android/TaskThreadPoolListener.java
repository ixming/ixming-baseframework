package org.ixming.task4android;

/*package*/ interface TaskThreadPoolListener {

	void beforeExecute(Thread t, Runnable r) ;
	
	void afterExecute(Runnable r, Throwable t) ;
	
	void terminated() ;
	
}
