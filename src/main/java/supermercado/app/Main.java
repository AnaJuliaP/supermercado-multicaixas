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
        setSize(900, 1200);
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
        caixasPanel.setBackground(new Color(240, 240, 240));
        JScrollPane scrollPane = new JScrollPane(caixasPanel);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Caixas em Operação"));
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
        controlPanel.setBackground(new Color(240, 240, 240));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Controles"));
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));

        abrirCaixaBtn = new JButton("➕ Abrir Novo Caixa");
        abrirCaixaBtn.setBackground(new Color(100, 200, 100));
        abrirCaixaBtn.setForeground(Color.WHITE);
        abrirCaixaBtn.setFont(new Font("Arial", Font.BOLD, 14));

        removerCaixaBtn = new JButton("➖ Remover Caixa");
        removerCaixaBtn.setBackground(new Color(200, 100, 100));
        removerCaixaBtn.setForeground(Color.WHITE);
        removerCaixaBtn.setFont(new Font("Arial", Font.BOLD, 14));

        // Inicialização correta do campo, sem redeclaração
        filaClientesArea = new JTextArea(10, 20);
        filaClientesArea.setEditable(false);
        filaClientesArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane filaScroll = new JScrollPane(filaClientesArea);
        filaScroll.setBorder(BorderFactory.createTitledBorder("Fila de Espera"));
        controlPanel.add(filaScroll);

        // Adiciona o JLabel da fila
        filaLabel = new JLabel("Fila de clientes: 0");
        filaLabel.setFont(new Font("Arial", Font.BOLD, 16));
        controlPanel.add(filaLabel);

        controlPanel.add(abrirCaixaBtn);
        controlPanel.add(removerCaixaBtn);
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
            abrirCaixaBtn.setBackground(new Color(200, 100, 100));
        } else {
            abrirCaixaBtn.setEnabled(true);
            abrirCaixaBtn.setText("➕ Abrir Novo Caixa");
            abrirCaixaBtn.setBackground(new Color(100, 200, 100));
        }

        // Atualizar botão de remover caixa
        if (caixas.size() <= CAIXAS_FIXOS) {
            removerCaixaBtn.setEnabled(false);
            removerCaixaBtn.setText("🔒 APENAS FIXOS");
            removerCaixaBtn.setBackground(new Color(150, 150, 150));
        } else {
            removerCaixaBtn.setEnabled(true);
            removerCaixaBtn.setText("➖ Remover Caixa");
            removerCaixaBtn.setBackground(new Color(200, 100, 100));
        }
    }

    private JPanel criarPainelCaixa(int numeroCaixa) {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Determinar se é caixa fixo ou dinâmico
        boolean isFixo = numeroCaixa <= CAIXAS_FIXOS;
        Color corBorda = isFixo ? new Color(0, 150, 0) : new Color(150, 150, 150);
        Color corFundo = isFixo ? new Color(220, 255, 220) : new Color(230, 245, 255);
        
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(corBorda, isFixo ? 3 : 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setBackground(corFundo);
        panel.setPreferredSize(new Dimension(800, 120));

        // Título do caixa com indicação de tipo
        String tituloTexto = "CAIXA " + numeroCaixa + (isFixo ? " (FIXO)" : " (DINÂMICO)");
        JLabel tituloCaixa = new JLabel(tituloTexto, SwingConstants.CENTER);
        tituloCaixa.setFont(new Font("Arial", Font.BOLD, 16));
        tituloCaixa.setForeground(isFixo ? new Color(0, 100, 0) : new Color(0, 80, 150));
        tituloCaixa.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // Painel de status
        JPanel statusPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        statusPanel.setBackground(corFundo);

        JLabel statusLabel = new JLabel("🔴 AGUARDANDO CLIENTE", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setForeground(new Color(0, 150, 0));
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
