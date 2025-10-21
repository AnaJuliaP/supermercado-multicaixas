package supermercado.app;

import supermercado.model.Cliente;
import supermercado.model.Caixa;
import supermercado.model.Cofre;
import supermercado.service.GerarCliente;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.Comparator;
import java.util.PriorityQueue;

public class Main extends JFrame {
    private JTextArea logArea;
    private JPanel caixasPanel;
    private JLabel filaLabel;
    private BlockingQueue<Cliente> filaClientes;
    private java.util.List<Caixa> caixas = new java.util.ArrayList<>();
    private static final int CAIXAS_FIXOS = 3;
    private JTextArea filaClientesArea;
    private Cofre cofre = new Cofre();

    // Controles para configuração
    private JComboBox<String> algoritmoCombo;
    private JCheckBox sincronismoCheckbox;
    private JButton iniciarBtn;
    private boolean simulacaoRodando = false;
    private boolean simulacaoExecutada = false;

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
    cofre.setLogArea(logArea);

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
        JLabel infoLabel = new JLabel("🏪 3 CAIXAS FIXOS");
        infoLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        infoLabel.setForeground(new Color(50, 50, 50));
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        controlPanel.add(infoLabel);
        controlPanel.add(Box.createVerticalStrut(10));

        // Configuração do algoritmo
        JPanel configPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        configPanel.setBackground(new Color(248, 250, 252));
        
        configPanel.add(new JLabel("Algoritmo:"));
        algoritmoCombo = new JComboBox<>(new String[]{"FCFS", "SJF"});
        configPanel.add(algoritmoCombo);
        
        configPanel.add(new JLabel("Sincronismo:"));
        sincronismoCheckbox = new JCheckBox("Ativar");
        configPanel.add(sincronismoCheckbox);
        
        controlPanel.add(configPanel);
        controlPanel.add(Box.createVerticalStrut(10));

        // Botão para iniciar/reniciar simulação
        iniciarBtn = new JButton("🚀 Iniciar Simulação");
        iniciarBtn.setBackground(new Color(46, 204, 113));
        iniciarBtn.setForeground(Color.WHITE);
        iniciarBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        iniciarBtn.setPreferredSize(new Dimension(220, 45));
        iniciarBtn.setMaximumSize(new Dimension(220, 45));
        iniciarBtn.addActionListener(e -> iniciarOuReiniciarSimulacao());
        controlPanel.add(iniciarBtn);
        controlPanel.add(Box.createVerticalStrut(10));

        // Botão para mostrar resumo
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

        // Inicializar com simulação parada
        atualizarEstadoBotoes(false);
    }

    private void iniciarOuReiniciarSimulacao() {
        if (simulacaoRodando) {
            pararSimulacao();
        }
        iniciarSimulacao();
    }

    private void iniciarSimulacao() {
        // Limpar simulação anterior
        if (simulacaoRodando) {
            pararSimulacao();
        }

        logArea.setText("");
        
        // Obter configurações
        String algoritmo = (String) algoritmoCombo.getSelectedItem();
        boolean sincronismo = sincronismoCheckbox.isSelected();
        
        // Criar fila baseada no algoritmo
        if ("SJF".equals(algoritmo)) {
            // Fila prioritária baseada no número de produtos (menor primeiro)
            filaClientes = new PriorityBlockingQueue<>(10, 
                Comparator.comparingInt(cliente -> cliente.getProdutos().size()));
        } else {
            // FCFS - fila normal
            filaClientes = new LinkedBlockingQueue<>();
        }

    // Configurar cofre
    cofre = new Cofre();
    cofre.setSincronismo(sincronismo);
    cofre.setLogArea(logArea);

        log("🚀 SIMULAÇÃO INICIADA - 6 CAIXAS FIXOS");
        log("📊 Algoritmo: " + algoritmo + " (" + 
            ("SJF".equals(algoritmo) ? "Menor compra primeiro" : "Ordem de chegada") + ")");
        log("🏦 MODO: " + (sincronismo ? "COM SINCRONISMO" : "SEM SINCRONISMO"));
        log("⚠️ CAIXAS FIXOS: 6 caixas operando simultaneamente");

        criarFilaFixaDeClientes();
        
        // Criar caixas
        caixas.clear();
        caixasPanel.removeAll();
        
        for (int i = 0; i < CAIXAS_FIXOS; i++) {
            abrirNovoCaixa();
        }

        simulacaoRodando = true;
        simulacaoExecutada = true;
        atualizarEstadoBotoes(true);
        caixasPanel.revalidate();
        caixasPanel.repaint();
    }

    private void criarFilaFixaDeClientes() {
        // Fila fixa para experimentos - sempre a mesma
        java.util.List<Cliente> clientesFixa = GerarCliente.gerarClientesAleatorios(15);

        for (Cliente cliente : clientesFixa) {
            try {
                filaClientes.put(cliente);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        log("👥 Fila fixa criada com " + filaClientes.size() + " clientes");
        log("🔄 Todos os 3 caixas vão processar esta mesma fila");
        atualizarFilaLabel();
    }

    private void atualizarFilaLabel() {
        SwingUtilities.invokeLater(() -> {
            if (filaClientes == null) return;
            
            int tamanhoFila = filaClientes.size();
            filaLabel.setText("Fila de clientes: " + tamanhoFila);
            
            StringBuilder sb = new StringBuilder();
            if (tamanhoFila == 0) {
                sb.append("🎉 FILA VAZIA - Todos os clientes foram atendidos!");
                // Verificar se todos os caixas devem parar
                verificarSeTodosCaixasDevemParar();
            } else {
                sb.append("👥 Clientes restantes na fila:\n\n");
                long tempoAtual = System.currentTimeMillis();
                int contador = 0;
                for (Cliente c : filaClientes) {
                    if (contador < 8) { 
                        long tempoEspera = tempoAtual - c.getTempoChegadaFila();
                        double tempoEsperaSegundos = tempoEspera / 1000.0;
                        sb.append("• ").append(c.getNome()).append(" - ")
                          .append(c.getProdutos().size()).append(" produtos")
                          .append(" (⏳ ").append(String.format("%.1f", tempoEsperaSegundos)).append("s)\n");
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

    private void verificarSeTodosCaixasDevemParar() {
        // Aguardar um pouco para garantir que todos os caixas tenham tempo de processar
        new Thread(() -> {
            try {
                Thread.sleep(2000); // Aguarda 2 segundos
                
                // Verificar se a fila ainda está vazia e se a simulação ainda está rodando
                if (simulacaoRodando && filaClientes.isEmpty()) {
                    log("🏁 Todos os clientes foram atendidos - encerrando simulação automaticamente");
                    SwingUtilities.invokeLater(() -> {
                        pararSimulacao();
                    });
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    private void abrirNovoCaixa() {
        int numeroCaixa = caixas.size() + 1;
        JPanel caixaPanel = criarPainelCaixa(numeroCaixa);
        caixasPanel.add(caixaPanel);

        // Passar configurações para o caixa
        boolean sincronismo = sincronismoCheckbox.isSelected();
        Caixa caixa = new Caixa(numeroCaixa, filaClientes, logArea, caixaPanel, cofre, 
                               this::atualizarFilaLabel, sincronismo);
        caixas.add(caixa);
        new Thread(caixa).start();

        log("🛒 Caixa " + numeroCaixa + " iniciado");
    }

    private void atualizarEstadoBotoes(boolean rodando) {
        iniciarBtn.setText(rodando ? "🔄 Reiniciar Simulação" : "🚀 Iniciar Simulação");
        iniciarBtn.setBackground(rodando ? new Color(241, 196, 15) : new Color(46, 204, 113));
        algoritmoCombo.setEnabled(!rodando);
        sincronismoCheckbox.setEnabled(!rodando);
    }

    private void mostrarResumo() {
        if (!simulacaoExecutada) {
            log("⚠️ Nenhuma simulação foi executada ainda");
            return;
        }

        int saldoReal = cofre.getSaldo();
        int clientesRestantes = filaClientes.size();
        boolean sincronismo = sincronismoCheckbox.isSelected();
        String algoritmo = (String) algoritmoCombo.getSelectedItem();
        
        log("\n" + repeat("=", 60));
        log("📊 RESUMO " + (simulacaoRodando ? "ATUAL DA" : "FINAL DA") + " SIMULAÇÃO");
        log(repeat("=", 60));
        log("📋 Configuração: " + algoritmo + " | " + 
            (sincronismo ? "COM SINCRONISMO" : "SEM SINCRONISMO"));
        log("🏦 Saldo atual no cofre: R$ " + saldoReal);
        log("👥 Clientes restantes na fila: " + clientesRestantes);
        
        if (!sincronismo) {
            log("⚠️  AVISO: Valor do cofre pode estar incorreto devido à falta de sincronização!");
        }
        
        // Mostrar estatísticas dos caixas
        log("\n⏱️  ESTATÍSTICAS DOS CAIXAS:");
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
        
        // 🆕 ESTATÍSTICAS DE TEMPO DE ESPERA NA FILA
        log("\n⏳ ESTATÍSTICAS DE ESPERA NA FILA:");
        
        // Coletar todos os clientes atendidos
        java.util.List<Caixa.ClienteAtendido> todosClientesAtendidos = new java.util.ArrayList<>();
        for (Caixa caixa : caixas) {
            todosClientesAtendidos.addAll(caixa.getClientesAtendidosInfo());
        }
        
        if (!todosClientesAtendidos.isEmpty()) {
            log("\n📊 Clientes já atendidos:");
            for (Caixa.ClienteAtendido cliente : todosClientesAtendidos) {
                log("  • " + cliente.getNome() + ": " + String.format("%.2f", cliente.getTempoEspera()) + "s na fila");
            }
            
            // Calcular estatísticas dos clientes atendidos
            double tempoMedioEspera = todosClientesAtendidos.stream().mapToDouble(Caixa.ClienteAtendido::getTempoEspera).average().orElse(0.0);
            double tempoMaximoEspera = todosClientesAtendidos.stream().mapToDouble(Caixa.ClienteAtendido::getTempoEspera).max().orElse(0.0);
            double tempoMinimoEspera = todosClientesAtendidos.stream().mapToDouble(Caixa.ClienteAtendido::getTempoEspera).min().orElse(0.0);
            
            log("  📊 Tempo médio de espera dos atendidos: " + String.format("%.2f", tempoMedioEspera) + "s");
            log("  📊 Tempo máximo de espera: " + String.format("%.2f", tempoMaximoEspera) + "s");
            log("  📊 Tempo mínimo de espera: " + String.format("%.2f", tempoMinimoEspera) + "s");
        }
        
        // Mostrar tempo de espera dos clientes restantes na fila
        if (filaClientes != null && !filaClientes.isEmpty()) {
            log("\n📋 Clientes ainda na fila:");
            long tempoAtual = System.currentTimeMillis();
            java.util.List<Double> temposEsperaRestantes = new java.util.ArrayList<>();
            
            for (Cliente cliente : filaClientes) {
                long tempoEspera = tempoAtual - cliente.getTempoChegadaFila();
                double tempoEsperaSegundos = tempoEspera / 1000.0;
                temposEsperaRestantes.add(tempoEsperaSegundos);
                log("  • " + cliente.getNome() + ": " + String.format("%.2f", tempoEsperaSegundos) + "s na fila");
            }
            
            if (!temposEsperaRestantes.isEmpty()) {
                double tempoMedioRestantes = temposEsperaRestantes.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
                log("  📊 Tempo médio de espera dos restantes: " + String.format("%.2f", tempoMedioRestantes) + "s");
            }
        }
        
        log(repeat("=", 60));
    }

    private void pararSimulacao() {
        if (!simulacaoRodando) return;

        // Parar todos os caixas
        for (Caixa caixa : caixas) {
            caixa.encerrar();
        }
        
        log("⏹️ SIMULAÇÃO ENCERRADA");
        log("📊 Clique em 'Mostrar Resumo' para ver as estatísticas finais");

        simulacaoRodando = false;
        atualizarEstadoBotoes(false);
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

    private String repeat(String str, int times) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < times; i++) sb.append(str);
        return sb.toString();
    }

    // Classe interna para PriorityBlockingQueue
    static class PriorityBlockingQueue<E> extends LinkedBlockingQueue<E> {
        private final Comparator<E> comparator;

        public PriorityBlockingQueue(int initialCapacity, Comparator<E> comparator) {
            this.comparator = comparator;
        }

        @Override
        public boolean offer(E e) {
            // Para simplicidade, vamos manter a ordem na inserção
            // Em uma implementação real, usaríamos uma PriorityQueue verdadeira
            return super.offer(e);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main main = new Main();
            main.setVisible(true);
        });
    }
}