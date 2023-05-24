package net.luis.utils.event;

/**
 *
 * @author Luis
 *
 */

public interface CancelableEvent extends Event {
	
	boolean isCanceled();
	
	void setCanceled(boolean canceled);
}
