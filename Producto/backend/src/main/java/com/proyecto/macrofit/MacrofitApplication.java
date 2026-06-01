package com.proyecto.macrofit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import java.net.ServerSocket;

@SpringBootApplication
public class MacrofitApplication {
	public static void main(String[] args) {
		SpringApplication.run(MacrofitApplication.class, args);
	}
}