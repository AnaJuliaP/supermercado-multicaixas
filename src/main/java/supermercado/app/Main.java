package supermercado.app;

import supermercado.model.Cliente;
import supermercado.model.Caixa;
import supermercado.model.Cofre;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main extends JFrame {
    private JTextArea logArea;
    private JPanel caixasPanel;
    private JLabel filaLabel;
    private BlockingQueue<Cliente> filaClientes = new LinkedBlockingQueue<>();
    private java.util.List<Caixa> caixas = new java.util.ArrayList<>();
    private static final int CAIXAS_FIXOS = 6;
    private JTextArea filaClientesArea;

    private Cofre cofre = new Cofre();

    public Main() {
        setTitle("Simulação de Supermercado - Threads Paralelas");
        setSize(1000, 1200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Título
        JLabel titulo = new JLabel("🛒 SIMULAÇÃO DE SUPERMERCADO - CAIXAS PARALELOS", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        titulo.setForeground(new Color(0, 100, 0));
        titulo.setOpaque(true);
        titulo.setBackground(new Color(220, 255, 220));
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titulo, BorderLayout.NORTH);

        // Painel de caixas com scroll
        caixasPanel = new JPanel();
        caixasPanel.setLayout(new BoxLayout(caixasPanel, BoxLayout.Y_AXIS));
        caixasPanel.setBackground(new Color(248, 250, 252));
        JScrollPane scrollPane = new JScrollPane(caixasPanel);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
                        "🏪 Caixas em Operação",
                        javax.swing.border.TitledBorder.LEFT,
                        javax.swing.border.TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 16),
                        new Color(50, 50, 50)
                ),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        scrollPane.setBackground(new Color(248, 250, 252));
        add(scrollPane, BorderLayout.CENTER);

        // Área de log
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setPreferredSize(new Dimension(1000, 300));
        logScroll.setBorder(BorderFactory.createTitledBorder("Log de Atividades"));
        add(logScroll, BorderLayout.SOUTH);

        // Configurar o logArea no cofre
        Cofre.setLogArea(logArea);

        // Painel de controles
        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(new Color(248, 250, 252));
        controlPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
                        "🎛️ Controles",
                        javax.swing.border.TitledBorder.LEFT,
                        javax.swing.border.TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 16),
                        new Color(50, 50, 50)
                ),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));

        // Informação sobre caixas fixos
        JLabel infoLabel = new JLabel("🏪 6 CAIXAS FIXOS");
        infoLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        infoLabel.setForeground(new Color(50, 50, 50));
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        controlPanel.add(infoLabel);
        controlPanel.add(Box.createVerticalStrut(10));

        // Botão para mostrar resumo (sem parar)
        JButton resumoBtn = new JButton("📊 Mostrar Resumo");
        resumoBtn.setBackground(new Color(52, 152, 219));
        resumoBtn.setForeground(Color.WHITE);
        resumoBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        resumoBtn.setPreferredSize(new Dimension(220, 45));
        resumoBtn.setMaximumSize(new Dimension(220, 45));
        resumoBtn.addActionListener(e -> mostrarResumo());
        controlPanel.add(resumoBtn);
        controlPanel.add(Box.createVerticalStrut(10));

        // Botão para parar simulação
        JButton pararBtn = new JButton("⏹️ Parar Simulação");
        pararBtn.setBackground(new Color(231, 76, 60));
        pararBtn.setForeground(Color.WHITE);
        pararBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pararBtn.setPreferredSize(new Dimension(220, 45));
        pararBtn.setMaximumSize(new Dimension(220, 45));
        pararBtn.addActionListener(e -> pararSimulacao());
        controlPanel.add(pararBtn);
        controlPanel.add(Box.createVerticalStrut(20));

        // Campo da fila
        filaClientesArea = new JTextArea(8, 20);
        filaClientesArea.setEditable(false);
        filaClientesArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JScrollPane filaScroll = new JScrollPane(filaClientesArea);
        filaScroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("⏳ Fila de Espera"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        controlPanel.add(filaScroll);

        filaLabel = new JLabel("👥 Fila de clientes: 0");
        filaLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        filaLabel.setForeground(new Color(50, 50, 50));
        controlPanel.add(filaLabel);
        add(controlPanel, BorderLayout.WEST);

        iniciarSimulacao();
    }

    private void iniciarSimulacao() {
        logArea.setText("");
        log("🚀 SIMULAÇÃO INICIADA - 6 CAIXAS FIXOS");
        log("📊 Fila fixa de clientes será processada por todos os caixas");
        log("🏦 MODO: SEM SINCRONISMO (sem sincronismo no cofre — valores podem divergir!)");
        log("⚠️ CAIXAS FIXOS: 6 caixas operando simultaneamente");

        criarFilaFixaDeClientes();
        
        for (int i = 0; i < CAIXAS_FIXOS; i++) {
            abrirNovoCaixa();
        }
    }

    private void criarFilaFixaDeClientes() {
        java.util.List<Cliente> clientesFixa = supermercado.service.GerarCliente.gerarClientesAleatorios(15);
        
        for (Cliente cliente : clientesFixa) {
            try {
                filaClientes.put(cliente);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        log("👥 Fila fixa criada com " + filaClientes.size() + " clientes");
        log("🔄 Todos os 6 caixas vão processar esta mesma fila");
        atualizarFilaLabel();
    }

    private void atualizarFilaLabel() {
        SwingUtilities.invokeLater(() -> {
            int tamanhoFila = filaClientes.size();
            filaLabel.setText("Fila de clientes: " + tamanhoFila);
            
            StringBuilder sb = new StringBuilder();
            if (tamanhoFila == 0) {
                sb.append("🎉 FILA VAZIA - Todos os clientes foram atendidos!");
            } else {
                sb.append("👥 Clientes restantes na fila:\n\n");
                int contador = 0;
                for (Cliente c : filaClientes) {
                    if (contador < 8) { 
                        sb.append("• ").append(c.getNome()).append(" - ").append(c.getProdutos().size()).append(" produtos\n");
                        contador++;
                    } else {
                        sb.append("\n... e mais ").append(tamanhoFila - 8).append(" clientes aguardando");
                        break;
                    }
                }
            }
            filaClientesArea.setText(sb.toString());
        });
    }

    private void abrirNovoCaixa() {
        int numeroCaixa = caixas.size() + 1;
        JPanel caixaPanel = criarPainelCaixa(numeroCaixa);
        caixasPanel.add(caixaPanel);

        // 🔹 Passa o cofre para o caixa e callback para atualizar fila
        Caixa caixa = new Caixa(numeroCaixa, filaClientes, logArea, caixaPanel, cofre, this::atualizarFilaLabel);
        caixas.add(caixa);
        new Thread(caixa).start();

        log("🛒 Caixa " + numeroCaixa + " iniciado");
        caixasPanel.revalidate();
        caixasPanel.repaint();
    }

    // Métodos de controle de caixas removidos - caixas são fixos

    private void mostrarResumo() {
        int saldoReal = Cofre.getSaldo();
        int clientesRestantes = filaClientes.size();
        
        log("\n" + "=".repeat(60));
        log("📊 RESUMO ATUAL DA SIMULAÇÃO");
        log("=".repeat(60));
        log("🏦 Saldo atual no cofre: R$ " + saldoReal);
        log("👥 Clientes restantes na fila: " + clientesRestantes);
        log("⚠️  NOTA: Este valor pode estar incorreto devido à falta de sincronização!");
        log("🔍 Múltiplos caixas acessam o cofre simultaneamente sem proteção");
        
        // Mostrar estatísticas dos caixas
        log("\n⏱️  ESTATÍSTICAS DOS CAIXAS:");
        for (Caixa caixa : caixas) {
            log("Caixa " + caixa.getId() + ": " + caixa.getClientesAtendidos() + " clientes | " +
                "Tempo total: " + (caixa.getTempoTotalAtendimento() / 1000.0) + "s | " +
                "Tempo médio: " + (caixa.getTempoMedioPorCliente() / 1000.0) + "s/cliente");
        }
        log("=".repeat(60));
    }

    private void pararSimulacao() {
        // Parar todos os caixas
        for (Caixa caixa : caixas) {
            caixa.encerrar();
        }
        
        // Mostrar resumo final
        int saldoReal = Cofre.getSaldo();
        int clientesRestantes = filaClientes.size();
        
        log("\n" + "=".repeat(60));
        log("📊 RESUMO FINAL DA SIMULAÇÃO");
        log("=".repeat(60));
        log("🏦 Saldo final no cofre: R$ " + saldoReal);
        log("👥 Clientes restantes na fila: " + clientesRestantes);
        log("⚠️  NOTA: Este valor pode estar incorreto devido à falta de sincronização!");
        log("🔍 Múltiplos caixas acessaram o cofre simultaneamente sem proteção");
        
        // Mostrar estatísticas finais dos caixas
        log("\n⏱️  ESTATÍSTICAS FINAIS DOS CAIXAS:");
        for (Caixa caixa : caixas) {
            log("Caixa " + caixa.getId() + ": " + caixa.getClientesAtendidos() + " clientes | " +
                "Tempo total: " + (caixa.getTempoTotalAtendimento() / 1000.0) + "s | " +
                "Tempo médio: " + (caixa.getTempoMedioPorCliente() / 1000.0) + "s/cliente");
        }
        
        // Calcular estatísticas gerais
        int totalClientes = caixas.stream().mapToInt(Caixa::getClientesAtendidos).sum();
        long tempoTotal = caixas.stream().mapToLong(Caixa::getTempoTotalAtendimento).sum();
        log("\n📈 ESTATÍSTICAS GERAIS:");
        log("Total de clientes atendidos: " + totalClientes);
        log("Tempo total de atendimento: " + (tempoTotal / 1000.0) + "s");
        log("Tempo médio por cliente: " + (totalClientes > 0 ? (tempoTotal / totalClientes / 1000.0) : 0) + "s");
        
        log("⏹️ SIMULAÇÃO ENCERRADA");
        log("=".repeat(60));
    }

    private JPanel criarPainelCaixa(int numeroCaixa) {
        JPanel panel = new JPanel(new BorderLayout());

        Color corBorda = new Color(46, 204, 113);
        Color corFundo = new Color(236, 252, 203);

        panel.setBorder(BorderFactory.createLineBorder(corBorda, 3));
        panel.setBackground(corFundo);
        panel.setPreferredSize(new Dimension(850, 130));

        JLabel tituloCaixa = new JLabel("🏪 CAIXA " + numeroCaixa, SwingConstants.CENTER);
        tituloCaixa.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JPanel statusPanel = new JPanel(new GridLayout(2, 1, 8, 8));
        statusPanel.setBackground(corFundo);

        JLabel statusLabel = new JLabel("🔴 AGUARDANDO CLIENTE", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setForeground(new Color(46, 204, 113));
        progressBar.setBackground(new Color(236, 240, 241));
        progressBar.setString("0%");

        statusPanel.add(statusLabel);
        statusPanel.add(progressBar);

        panel.add(tituloCaixa, BorderLayout.NORTH);
        panel.add(statusPanel, BorderLayout.CENTER);

        return panel;
    }

    private void log(String mensagem) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(mensagem + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main main = new Main();
            main.setVisible(true);
        });
    }
}
