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
    private Cofre cofre; 
    private Runnable atualizarFilaCallback;
    private boolean sincronismo = false;
    private long tempoInicio;
    private long tempoTotalAtendimento = 0;
    private int clientesAtendidos = 0;
    
    // 🆕 Lista para armazenar informações dos clientes atendidos
    private java.util.List<ClienteAtendido> clientesAtendidosInfo = new java.util.ArrayList<>();

    public Caixa(int id, BlockingQueue<Cliente> fila, JTextArea logArea, JPanel caixaPanel, Cofre cofre, Runnable atualizarFilaCallback, boolean sincronismo) {
        this.id = id;
        this.fila = fila;
        this.logArea = logArea;
        this.caixaPanel = caixaPanel;
        this.cofre = cofre;
        this.atualizarFilaCallback = atualizarFilaCallback;
        this.sincronismo = sincronismo;
        // Propagar configuração para o cofre também
        if (this.cofre != null) {
            this.cofre.setSincronismo(this.sincronismo);
        }
    }

    @Override
    public void run() {
        tempoInicio = System.currentTimeMillis();
        while (ativo) {
            try {
                // 🧍‍♀️ Gera cliente se a fila estiver vazia
                if (fila.isEmpty()) {
                    log("⏹️ Caixa " + id + " parou - fila vazia");
                    atualizarStatus("⏹️ FILA VAZIA - CAIXA PARADO");
                    break;
                }

                Cliente cliente = fila.take();
                long inicioAtendimento = System.currentTimeMillis();
                
                // 🕐 Calcular tempo de espera na fila
                long tempoEsperaFila = inicioAtendimento - cliente.getTempoChegadaFila();
                double tempoEsperaSegundos = tempoEsperaFila / 1000.0;
                
                // 🆕 Armazenar informações do cliente atendido
                clientesAtendidosInfo.add(new ClienteAtendido(cliente.getNome(), tempoEsperaSegundos));
                
                atualizarStatus("🟢 ATENDENDO: " + cliente.getNome());
                log("📌 Caixa " + id + " iniciou atendimento: " + cliente.getNome() + 
                    " (⏳ Esperou na fila: " + String.format("%.2f", tempoEsperaSegundos) + "s)");
                
                if (atualizarFilaCallback != null) {
                    atualizarFilaCallback.run();
                }

                int totalProdutos = cliente.getProdutos().size();
                int produtosProcessados = 0;

                for (Produto produto : cliente.getProdutos()) {
                    Thread.sleep(800);
                    produtosProcessados++;

                    int progresso = (int) ((produtosProcessados * 100.0) / totalProdutos);
                    atualizarProgresso(progresso);

                    log("➡️ Caixa " + id + " processou: " + produto.getNome() +
                            " (" + 800 + "ms)");
                }

                long fimAtendimento = System.currentTimeMillis();
                long tempoAtendimento = fimAtendimento - inicioAtendimento;
                tempoTotalAtendimento += tempoAtendimento;
                clientesAtendidos++;
                
                log("✅ Caixa " + id + " finalizou: " + cliente.getNome() +
                        " - Total: " + totalProdutos + " produtos" +
                        " - Tempo atendimento: " + (tempoAtendimento / 1000.0) + "s" +
                        " - Tempo espera fila: " + String.format("%.2f", tempoEsperaSegundos) + "s");

                // 💰 Simula o valor total da compra
                double totalCompra = cliente.getProdutos().stream()
                        .mapToDouble(p -> p.getTempoProcessamento() / 1000.0)
                        .sum();

                try {
                    Thread.sleep(500 + random.nextInt(500)); 
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                // 💸 Deposita o valor no cofre (sem sincronização → erros de soma possíveis)
                int valorInteiro = (int) Math.round(totalCompra);
                log("🏃 Caixa " + id + " correndo para o cofre com R$ " + String.format("%.2f", totalCompra));
                cofre.depositar(valorInteiro, id);

                atualizarStatus("🔴 AGUARDANDO CLIENTE");
                atualizarProgresso(0);

                Thread.sleep(1000);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log("⚠️ Erro no caixa " + id + ": " + e.getMessage());
            }
        }

        atualizarStatus("⛔ CAIXA ENCERRADO");
        atualizarProgresso(0);
    }

    private void atualizarStatus(String status) {
        SwingUtilities.invokeLater(() -> {
            try {
                JPanel statusPanel = (JPanel) caixaPanel.getComponent(1);
                JLabel statusLabel = (JLabel) statusPanel.getComponent(0);
                statusLabel.setText(status);
            } catch (Exception e) {
                log("⚠️ Erro ao atualizar status do caixa " + id);
            }
        });
    }

    private void atualizarProgresso(int progresso) {
        SwingUtilities.invokeLater(() -> {
            try {
                JPanel statusPanel = (JPanel) caixaPanel.getComponent(1);
                JProgressBar progressBar = (JProgressBar) statusPanel.getComponent(1);
                progressBar.setValue(progresso);
                progressBar.setString(progresso + "%");
            } catch (Exception e) {
                log("⚠️ Erro ao atualizar progresso do caixa " + id);
            }
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
    
    public long getTempoTotalAtendimento() {
        return tempoTotalAtendimento;
    }
    
    public int getClientesAtendidos() {
        return clientesAtendidos;
    }
    
    public long getTempoMedioPorCliente() {
        return clientesAtendidos > 0 ? tempoTotalAtendimento / clientesAtendidos : 0;
    }

    // 🆕 Métodos para acessar informações dos clientes atendidos
    public java.util.List<ClienteAtendido> getClientesAtendidosInfo() {
        return new java.util.ArrayList<>(clientesAtendidosInfo);
    }
    
    public double getTempoMedioEsperaFila() {
        if (clientesAtendidosInfo.isEmpty()) return 0.0;
        return clientesAtendidosInfo.stream().mapToDouble(ClienteAtendido::getTempoEspera).average().orElse(0.0);
    }
    
    public double getTempoMaximoEsperaFila() {
        return clientesAtendidosInfo.stream().mapToDouble(ClienteAtendido::getTempoEspera).max().orElse(0.0);
    }
    
    public double getTempoMinimoEsperaFila() {
        return clientesAtendidosInfo.stream().mapToDouble(ClienteAtendido::getTempoEspera).min().orElse(0.0);
    }
    
    // 🆕 Classe interna para armazenar informações do cliente atendido
    public static class ClienteAtendido {
        private final String nome;
        private final double tempoEspera;
        
        public ClienteAtendido(String nome, double tempoEspera) {
            this.nome = nome;
            this.tempoEspera = tempoEspera;
        }
        
        public String getNome() { return nome; }
        public double getTempoEspera() { return tempoEspera; }
    }
}
