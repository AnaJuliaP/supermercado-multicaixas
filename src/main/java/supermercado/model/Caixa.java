package supermercado.model;

import supermercado.service.GerarCliente;

import javax.swing.*;
import java.util.concurrent.BlockingQueue;
import java.util.Random;

public class Caixa implements Runnable {
    private int id;
    private BlockingQueue<Cliente> fila;
    private JTextArea logArea;
    private JPanel caixaPanel;
    private volatile boolean ativo = true;
    private final Random random = new Random();

    public Caixa(int id, BlockingQueue<Cliente> fila, JTextArea logArea, JPanel caixaPanel) {
        this.id = id;
        this.fila = fila;
        this.logArea = logArea;
        this.caixaPanel = caixaPanel;
    }

    @Override
    public void run() {
        while (ativo) {
            try {
                // Gerar cliente aleatório se a fila estiver vazia
                if (fila.isEmpty()) {
                    Cliente novoCliente = GerarCliente.gerarClientesAleatorios(1).get(0);
                    fila.put(novoCliente);
                }

                // Processar cliente
                Cliente cliente = fila.take();
                atualizarStatus("🟢 ATENDENDO: " + cliente.getNome());
                log("📌 Caixa " + id + " iniciou atendimento: " + cliente.getNome());

                int totalProdutos = cliente.getProdutos().size();
                int produtosProcessados = 0;

                for (Produto produto : cliente.getProdutos()) {
                    Thread.sleep(produto.getTempoProcessamento());
                    produtosProcessados++;

                    int progresso = (int) ((produtosProcessados * 100.0) / totalProdutos);
                    atualizarProgresso(progresso);

                    log("➡️ Caixa " + id + " processou: " + produto.getNome() +
                            " (" + produto.getTempoProcessamento() + "ms)");
                }

                log("✅ Caixa " + id + " finalizou: " + cliente.getNome() +
                        " - Total: " + totalProdutos + " produtos");

                atualizarStatus("🔴 AGUARDANDO CLIENTE");
                atualizarProgresso(0);

                // Pequena pausa entre clientes
                Thread.sleep(500);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void atualizarStatus(String status) {
        SwingUtilities.invokeLater(() -> {
            JLabel statusLabel = (JLabel) ((JPanel) caixaPanel.getComponent(1)).getComponent(0);
            statusLabel.setText(status);
        });
    }

    private void atualizarProgresso(int progresso) {
        SwingUtilities.invokeLater(() -> {
            JProgressBar progressBar = (JProgressBar) ((JPanel) caixaPanel.getComponent(1)).getComponent(1);
            progressBar.setValue(progresso);
            progressBar.setString(progresso + "%");
        });
    }

    private void log(String msg) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(msg + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    public void encerrar() {
        ativo = false;
    }

    public int getId() {
        return id;
    }
}