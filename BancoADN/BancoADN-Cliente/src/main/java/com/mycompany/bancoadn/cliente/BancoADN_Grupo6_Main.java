/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.bancoadn.cliente;

import com.mycompany.bancoadn.cliente.httpapi.ApiServer;

/**
 * Entry point of the SimpleADN client application.
 * 
 * Starts the HTTP server that exposes the existing socket-based functionality
 * as REST endpoints for the React frontend.
 */
public class BancoADN_Grupo6_Main {

    public static void main(String[] args) {
        // Start the HTTP server on port 7000
        ApiServer server = new ApiServer();
        server.start(7000);
        
        // Keep the application running (the server runs in its own threads)
        // In a real application, you might want to add a shutdown hook.
        System.out.println("HTTP server running on port 7000. Press Ctrl+C to stop.");
        // Wait indefinitely
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
