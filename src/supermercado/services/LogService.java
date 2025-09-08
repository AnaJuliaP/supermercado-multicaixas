package supermercado.services;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LogService {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static void log(String mensagem) {
        String hora = LocalTime.now().format(formatter);
        System.out.println("[" + hora + "] " + mensagem);
    }
}
