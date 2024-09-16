package dev.memory_game;

import static spark.Spark.port;

import java.sql.Connection;
import java.util.concurrent.ExecutorService;

import dev.memory_game.network.SocketServer;
import dev.memory_game.utils.ThreadPoolManager;
import dev.memory_game.controllers.RoutesController;
// import dev.memory_game.network.SocketClient;

public class Main {

  public static void main(String[] args) {

    // Start the database connection
    Connection connection = DbConnection.getConnection();

    ////////////////////////////////////////////////////

    // Init the ThreadPool to do asynchronous tasks
    ExecutorService threadPool = ThreadPoolManager.getThreadPool();

    ///////////////////////////////////////////////////////

    // Set the HTTP server port for Spark
    // Spark already out of the box with threadpool so no need to configure the
    // thread pool to spark
    int httpPort = 8081;
    port(httpPort);

    // HTTP server - REST API
    new RoutesController(connection, threadPool);

    /////////////////////////////////////////////////

    // Socket server

    // Start the socket server
    int port = 8082; // Choose your desired port
    SocketServer server = new SocketServer(port, threadPool, connection);
    new Thread(() -> server.start()).start();

    System.out.println("Socket server started on port " + port);

    // // // Start the socket client - THE CLIENT WILL BE SEPARATED TO A DIFFERENT
    // PROJECT THAT SEPECIFY FOR THE FRONTEND, SO ONLY UNCOMMENTED THIS IF YOU WANT
    // TO TEST IF THE CLIENT CAN CONNECT TO THE SERVER FROM THIS PROJECT.

    // SocketClient client = new SocketClient("127.0.0.1", port);
    // new Thread(() -> client.start()).start();

    ///////////////////////////////////////////////

    // Add a shutdown threadpool when program close
    Runtime.getRuntime().addShutdownHook(new Thread(ThreadPoolManager::shutdown));
  }
}
