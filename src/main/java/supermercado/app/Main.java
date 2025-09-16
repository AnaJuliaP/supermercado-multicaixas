package supermercado.app;

import supermercado.model.Cliente;
import supermercado.model.Caixa;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main extends JFrame {
    private JTextArea logArea;
    private JPanel caixasPanel;
    private JButton abrirCaixaBtn;
    private JButton removerCaixaBtn;
    private JLabel filaLabel;
    private BlockingQueue<Cliente> filaClientes = new LinkedBlockingQueue<>();
    private java.util.List<Caixa> caixas = new java.util.ArrayList<>();
    private static final int LIMITE_CAIXAS = 10;
    private static final int CAIXAS_FIXOS = 2; // Dois primeiros caixas são fixos
    private JTextArea filaClientesArea;

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

        // Botão Abrir Caixa com estilo moderno
        abrirCaixaBtn = new JButton("➕ Abrir Novo Caixa");
        abrirCaixaBtn.setBackground(new Color(46, 204, 113)); // Verde moderno
        abrirCaixaBtn.setForeground(Color.WHITE);
        abrirCaixaBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        abrirCaixaBtn.setPreferredSize(new Dimension(220, 45));
        abrirCaixaBtn.setMaximumSize(new Dimension(220, 45));
        abrirCaixaBtn.setFocusPainted(false);
        abrirCaixaBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));

        // Botão Remover Caixa com estilo moderno
        removerCaixaBtn = new JButton("➖ Remover Caixa");
        removerCaixaBtn.setBackground(new Color(231, 76, 60)); // Vermelho moderno
        removerCaixaBtn.setForeground(Color.WHITE);
        removerCaixaBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        removerCaixaBtn.setPreferredSize(new Dimension(220, 45));
        removerCaixaBtn.setMaximumSize(new Dimension(220, 45));
        removerCaixaBtn.setFocusPainted(false);
        removerCaixaBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));

        // Adicionar botões primeiro (acima da fila de espera)
        controlPanel.add(abrirCaixaBtn);
        controlPanel.add(Box.createVerticalStrut(10)); 
        controlPanel.add(removerCaixaBtn);
        controlPanel.add(Box.createVerticalStrut(10)); 

        // Inicialização correta do campo, sem redeclaração
        filaClientesArea = new JTextArea(8, 20);
        filaClientesArea.setEditable(false);
        filaClientesArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        filaClientesArea.setBackground(new Color(255, 255, 255));
        filaClientesArea.setForeground(new Color(50, 50, 50));
        filaClientesArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        JScrollPane filaScroll = new JScrollPane(filaClientesArea);
        filaScroll.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                "⏳ Fila de Espera",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12),
                new Color(70, 70, 70)
            ),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        controlPanel.add(filaScroll);

        // Adiciona o JLabel da fila com estilo moderno
        filaLabel = new JLabel("👥 Fila de clientes: 0");
        filaLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        filaLabel.setForeground(new Color(50, 50, 50));
        filaLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        controlPanel.add(filaLabel);
        add(controlPanel, BorderLayout.WEST);

        // Eventos
        abrirCaixaBtn.addActionListener(e -> abrirNovoCaixa());
        removerCaixaBtn.addActionListener(e -> removerUltimoCaixa());

        // Iniciar simulação com 2 caixas fixos
        iniciarSimulacao();
    }

    private void iniciarSimulacao() {
        logArea.setText("");
        log("🚀 SIMULAÇÃO INICIADA - 2 CAIXAS FIXOS");
        log("📊 Clientes são gerados automaticamente e distribuídos para os caixas");
        log("⚠️ LIMITE MÁXIMO: " + LIMITE_CAIXAS + " caixas - Simulação será encerrada quando atingir o limite");
        log("⚠️ CAIXAS FIXOS: Os 2 primeiros caixas não podem ser removidos");

        // Criar dois caixas iniciais (fixos)
        abrirNovoCaixa();
        abrirNovoCaixa();

        // Iniciar thread de geração de clientes aleatórios
        iniciarGeradorDeClientes();
    }

    private void iniciarGeradorDeClientes() {
        Thread gerador = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000 + (int)(Math.random() * 2000)); // 1 a 3 segundos
                    Cliente novoCliente = supermercado.service.GerarCliente.gerarClientesAleatorios(1).get(0);
                    filaClientes.put(novoCliente);
                    log("👤 Novo cliente chegou: " + novoCliente.getNome());
                    atualizarFilaLabel();
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        gerador.setDaemon(true);
        gerador.start();
    }

    private void atualizarFilaLabel() {
        SwingUtilities.invokeLater(() -> {
            filaLabel.setText("Fila de clientes: " + filaClientes.size());
            StringBuilder sb = new StringBuilder();
            long agora = System.currentTimeMillis();
            for (Cliente c : filaClientes) {
                long espera = (agora - c.getTempoChegadaFila()) / 1000;
                sb.append(c.getNome()).append(" - Espera: ").append(espera).append("s\n");
            }
            filaClientesArea.setText(sb.toString());
        });
    }

    private void abrirNovoCaixa() {
        // Verificar se já atingiu o limite de caixas
        if (caixas.size() >= LIMITE_CAIXAS) {
            log("⚠️ LIMITE MÁXIMO DE CAIXAS ATINGIDO! (" + LIMITE_CAIXAS + " caixas)");
            log("🛑 SIMULAÇÃO ENCERRADA - Não é possível abrir mais caixas");
            abrirCaixaBtn.setEnabled(false);
            abrirCaixaBtn.setText("❌ LIMITE ATINGIDO");
            abrirCaixaBtn.setBackground(new Color(200, 100, 100));
            return;
        }

        // Encontrar o próximo número disponível na sequência
        int numeroCaixa = encontrarProximoNumeroDisponivel();
        JPanel caixaPanel = criarPainelCaixa(numeroCaixa);
        caixasPanel.add(caixaPanel);

        Caixa caixa = new Caixa(numeroCaixa, filaClientes, logArea, caixaPanel);
        caixas.add(caixa);
        new Thread(caixa).start();

        String tipoCaixa = (numeroCaixa <= CAIXAS_FIXOS) ? " (FIXO)" : " (DINÂMICO)";
        log("🛒 NOVO CAIXA ABERTO: Caixa " + numeroCaixa + tipoCaixa + " (Total: " + caixas.size() + "/" + LIMITE_CAIXAS + ")");
        caixasPanel.revalidate();
        caixasPanel.repaint();

        // Atualizar estado dos botões
        atualizarEstadoBotoes();

        // Verificar se atingiu o limite após adicionar o caixa
        if (caixas.size() >= LIMITE_CAIXAS) {
            log("⚠️ LIMITE MÁXIMO DE CAIXAS ATINGIDO! (" + LIMITE_CAIXAS + " caixas)");
            log("🛑 SIMULAÇÃO ENCERRADA - Não é possível abrir mais caixas");
        }
    }

    private int encontrarProximoNumeroDisponivel() {
        // Se não há caixas, começar com 1
        if (caixas.isEmpty()) {
            return 1;
        }

        // Criar lista com os números dos caixas existentes
        java.util.List<Integer> numerosExistentes = new java.util.ArrayList<>();
        for (Caixa caixa : caixas) {
            numerosExistentes.add(caixa.getId());
        }

        // Procurar o primeiro número disponível a partir de 1
        for (int i = 1; i <= LIMITE_CAIXAS; i++) {
            if (!numerosExistentes.contains(i)) {
                return i;
            }
        }

        // Se chegou aqui, todos os números estão ocupados (não deveria acontecer)
        return caixas.size() + 1;
    }

    private void removerUltimoCaixa() {
        // Verificar se há caixas para remover (apenas os dinâmicos)
        if (caixas.size() <= CAIXAS_FIXOS) {
            log("🔒 CAIXAS FIXOS PROTEGIDOS!");
            log("❌ Não é possível remover os " + CAIXAS_FIXOS + " primeiros caixas - Eles são fixos e não podem ser removidos");
            return;
        }

        // Remover o último caixa da lista (apenas dinâmicos)
        Caixa caixaRemovido = caixas.remove(caixas.size() - 1);
        caixaRemovido.encerrar();
        
        // Remover o painel do caixa da interface
        caixasPanel.remove(caixasPanel.getComponentCount() - 1);
        
        log("🗑️ CAIXA REMOVIDO: Caixa " + caixaRemovido.getId() + " (DINÂMICO) (Total: " + caixas.size() + "/" + LIMITE_CAIXAS + ")");
        log("⚠️ Clientes em atendimento no caixa removido foram transferidos para outros caixas");
        
        caixasPanel.revalidate();
        caixasPanel.repaint();
        
        // Atualizar estado dos botões
        atualizarEstadoBotoes();
    }

    private void atualizarEstadoBotoes() {
        // Atualizar botão de abrir caixa
        if (caixas.size() >= LIMITE_CAIXAS) {
            abrirCaixaBtn.setEnabled(false);
            abrirCaixaBtn.setText("❌ LIMITE ATINGIDO");
            abrirCaixaBtn.setBackground(new Color(149, 165, 166));
        } else {
            abrirCaixaBtn.setEnabled(true);
            abrirCaixaBtn.setText("➕ Abrir Novo Caixa");
            abrirCaixaBtn.setBackground(new Color(46, 204, 113));
        }

        // Atualizar botão de remover caixa
        if (caixas.size() <= CAIXAS_FIXOS) {
            removerCaixaBtn.setEnabled(false);
            removerCaixaBtn.setText("🔒 APENAS FIXOS");
            removerCaixaBtn.setBackground(new Color(149, 165, 166));
        } else {
            removerCaixaBtn.setEnabled(true);
            removerCaixaBtn.setText("➖ Remover Caixa");
            removerCaixaBtn.setBackground(new Color(231, 76, 60));
        }
    }

    private JPanel criarPainelCaixa(int numeroCaixa) {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Determinar se é caixa fixo ou dinâmico
        boolean isFixo = numeroCaixa <= CAIXAS_FIXOS;
        Color corBorda = isFixo ? new Color(46, 204, 113) : new Color(52, 152, 219);
        Color corFundo = isFixo ? new Color(236, 252, 203) : new Color(235, 245, 255);
        
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(corBorda, isFixo ? 3 : 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        panel.setBackground(corFundo);
        panel.setPreferredSize(new Dimension(850, 130));

        // Título do caixa com indicação de tipo
        String tituloTexto = "🏪 CAIXA " + numeroCaixa + (isFixo ? " (FIXO)" : " (DINÂMICO)");
        JLabel tituloCaixa = new JLabel(tituloTexto, SwingConstants.CENTER);
        tituloCaixa.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tituloCaixa.setForeground(isFixo ? new Color(39, 174, 96) : new Color(41, 128, 185));
        tituloCaixa.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        // Painel de status
        JPanel statusPanel = new JPanel(new GridLayout(2, 1, 8, 8));
        statusPanel.setBackground(corFundo);

        JLabel statusLabel = new JLabel("🔴 AGUARDANDO CLIENTE", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        statusLabel.setForeground(new Color(231, 76, 60));

        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setForeground(new Color(46, 204, 113));
        progressBar.setBackground(new Color(236, 240, 241));
        progressBar.setString("0%");
        progressBar.setFont(new Font("Segoe UI", Font.BOLD, 11));

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
