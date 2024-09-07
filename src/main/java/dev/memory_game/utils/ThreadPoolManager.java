package dev.memory_game.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolManager {

  private static ExecutorService threadPool;

  private ThreadPoolManager() {
    // No constructor instant needed
  }

  public static synchronized ExecutorService getThreadPool() {
    if (threadPool == null) {
      threadPool = Executors.newFixedThreadPool(16);
    }
    return threadPool;
  }

  public static void shutdown() {
    if (threadPool != null && !threadPool.isShutdown()) {
      threadPool.shutdown();
    }
  }

}
