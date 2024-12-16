package it.viligiardi;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Server avviato");
        ServerSocket ss1 = new ServerSocket(8080);
        while (true) {
            Socket s1 = ss1.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(s1.getInputStream()));
            DataOutputStream out = new DataOutputStream(s1.getOutputStream());

            String[] firstLine = in.readLine().split(" ");
            if (firstLine.length < 3) {
                continue; //Controllo per vedere se la richiesta non è valida, e nel caso lo fosse ignora
            }

            String method = firstLine[0]; //Va ad indicare il metodo utilizzato (GET/POST)
            String resource = firstLine[1]; //Va ad indicare il percorso dell'URL richiesto
            String version = firstLine[2]; //Va ad indicare la versione del protocollo HTTP, ad esempio HTTP/1.1
            System.out.println(method);
            System.out.println(resource);

            String header;
            while ((header = in.readLine()) != null && !header.isEmpty()) {
                System.out.println(header);
            }

            System.out.println("richiesta terminata");

            //Determina il file da mandare
            File f = new File("demo/Progetto_Viligiardi" + resource);
            if (f.exists()) {
                //determina il tipo del file
                String[] typeFile = resource.split("\\.");
                String textType = "text/html"; //Come default ho messo l'estenzione del file .html
                if (typeFile.length > 1) {
                    String extension = typeFile[typeFile.length - 1];
                    switch (extension.toLowerCase()) {
                        case "css":
                            textType = "text/css";
                            break;
                        case "js":
                            textType = "application/javascript";
                            break;
                        case "jpg":
                            textType = "images/jpg";
                        case "jpeg":
                            textType = "images/jpeg";
                            break;
                        case "png":
                            textType = "images/png";
                            break;
                        case "webp":
                            textType = "images/webp";
                            break;

                        default : 
                            textType = "text/html";
                            break;
                    }
                }

                //Invia la risposta HTTP con il file richiesto
                out.writeBytes("HTTP/1.1 200 OK\n");
                out.writeBytes("Content-Type: " + textType + "\n");
                out.writeBytes("Content-Length: " + f.length() + "\n");
                out.writeBytes("\n");

                InputStream input = new FileInputStream(f);
                byte[] buf = new byte[8192];
                int n;
                while ((n = input.read(buf)) != -1) {
                    out.write(buf, 0, n);
                }
                input.close();
            } else {
                //Se il file non esiste o non viene trovato, invia una risposta 404
                String responseBody = "Errore: file non trovato.";
                out.writeBytes("HTTP/1.1 404 Not Found\n");
                out.writeBytes("Content-Type: text/plain\n");
                out.writeBytes("Content-Length: " + responseBody.length() + "\n");
                out.writeBytes("\n");
                out.writeBytes(responseBody);
            }
            s1.close();
        }
    }
}