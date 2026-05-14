package com.proyecto.macrofit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import java.net.ServerSocket;

@SpringBootApplication
public class MacrofitApplication {

	public static void main(String[] args) {
		// Iniciar el túnel SSH antes de arrancar Spring Boot (Revisar que puerto se esta usando)
		if (isPortAvailable(3399)) {
			try {
				// Capturar variables del sistema
				String llavePrivada = System.getenv("SSH_KEY_PATH");
				String ipServidor = System.getenv("SSH_HOST");
				String sshUser = System.getenv("SSH_USER");

				if (llavePrivada == null || ipServidor == null) {
					System.err.println("Faltan variables de entorno para el túnel SSH.");
					return;
				}

				System.out.println("Iniciando conexión segura con Oracle Cloud...");
				JSch jsch = new JSch();
				jsch.addIdentity(llavePrivada);
				Session session = jsch.getSession(sshUser, ipServidor, 22);

				session.setConfig("StrictHostKeyChecking", "no");
				session.connect(10000);
				session.setPortForwardingL(3399, "127.0.0.1", 3306);

				System.out.println("¡Túnel SSH establecido con éxito! Arrancando Spring Boot...");

			} catch (Exception e) {
				System.err.println("Error fatal al crear el túnel SSH: " + e.getMessage());
				// Si el túnel falla, no inicia el programa
				return;
			}
		} else {
			System.out.println("El puerto 3306 ya está en uso. Asumiendo que el túnel sigue abierto...");
		}

		SpringApplication.run(MacrofitApplication.class, args);
	}

	private static boolean isPortAvailable(int port) {
		try (ServerSocket ignored = new ServerSocket(port)) {
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}