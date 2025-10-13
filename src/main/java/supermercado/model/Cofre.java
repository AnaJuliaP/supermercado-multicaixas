package supermercado.model;

import javax.swing.JTextArea;

public class Cofre {
    private static int saldo = 0;
    private static JTextArea logArea;

    public static void setLogArea(JTextArea area) {
        logArea = area;
    }

    public static void depositar(int valor, int idCaixa) {
        log("🚪 Caixa " + idCaixa + " entrou no cofre com R$" + valor);
        
        int saldoAnterior = saldo;

        try {
            Thread.sleep(2000); 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        saldo = saldoAnterior + valor;

        log("💰 Caixa " + idCaixa + " depositou R$" + valor + 
            " | Saldo anterior: R$" + saldoAnterior + " → Novo saldo: R$" + saldo);
    }

    private static void log(String mensagem) {
        if (logArea != null) {
            javax.swing.SwingUtilities.invokeLater(() -> {
                logArea.append(mensagem + "\n");
                logArea.setCaretPosition(logArea.getDocument().getLength());
            });
        }
    }

    public static int getSaldo() {
        return saldo;
    }
}

