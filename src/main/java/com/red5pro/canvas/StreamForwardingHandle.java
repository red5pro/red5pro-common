package com.red5pro.canvas;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

/**
 * Handle to an ongoing stream forwarding operation.
 * Returned by {@link Red5ProCanvas#forward} to allow callers to:
 * <ul>
 *   <li>Wait for forwarding to start</li>
 *   <li>Register for death notifications</li>
 *   <li>Check health and stop forwarding</li>
 * </ul>
 *
 * @author Nate Roe
 */
public interface StreamForwardingHandle {

    /**
     * Blocks until forwarding has started or failed.
     *
     * @param timeout maximum time to wait
     * @param unit time unit for timeout
     * @throws ForwardingException if forwarding failed to start
     * @throws TimeoutException if timeout expires before forwarding starts
     * @throws InterruptedException if thread is interrupted while waiting
     */
    void awaitStarted(long timeout, TimeUnit unit) throws ForwardingException, TimeoutException, InterruptedException;

    /**
     * Register a handler to be called if forwarding dies after successfully starting.
     * The handler receives the cause of death (never null).
     * <p>
     * This handler is NOT called when {@link #stop()} is invoked - only for unexpected
     * termination (connection closed, remote unpublish, exceptions, etc.).
     * <p>
     * Only one handler can be registered; subsequent calls replace the previous handler.
     * <p>
     * Note: Prefer passing the death handler to {@link Red5ProCanvas#forward} instead of
     * calling this method, to ensure the handler is registered before any async work begins.
     *
     * @param handler callback invoked on forwarding death
     */
    void onDeath(Consumer<Throwable> handler);

    /**
     * Check if forwarding is currently active.
     *
     * @return true if forwarding is running, false if not started, failed, or stopped
     */
    boolean isActive();

    /**
     * Stop forwarding. Safe to call multiple times or if already stopped.
     * Does not trigger the {@link #onDeath} handler.
     */
    void stop();
}
