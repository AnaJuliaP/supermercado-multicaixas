package supermercado.model;

import javax.swing.JTextArea;

public class Cofre {
    private int saldo = 0;
    private JTextArea logArea;
    // controla se operações no cofre devem ser sincronizadas
    private boolean sincronizado = false;

    public void setLogArea(JTextArea area) {
        this.logArea = area;
    }

    /**
     * Define se o cofre deve usar sincronização ao atualizar o saldo.
     */
    public void setSincronismo(boolean ativo) {
        this.sincronizado = ativo;
    }

    public void depositar(int valor, int idCaixa) {
        log("🚪 Caixa " + idCaixa + " entrou no cofre com R$" + valor);

        if (sincronizado) {
            synchronized (this) {
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
        } else {
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
    }

    private void log(String mensagem) {
        if (logArea != null) {
            javax.swing.SwingUtilities.invokeLater(() -> {
                logArea.append(mensagem + "\n");
                logArea.setCaretPosition(logArea.getDocument().getLength());
            });
        }
    }

    public int getSaldo() {
        return saldo;
    }
}

