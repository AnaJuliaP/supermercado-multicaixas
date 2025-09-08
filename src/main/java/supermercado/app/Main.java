package supermercado.app;

import supermercado.model.Cliente;
import supermercado.model.Caixa;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Main extends JFrame {
    private JTextArea logArea;
    private JPanel caixasPanel;
    private JButton abrirCaixaBtn;
    private BlockingQueue<Cliente> filaClientes = new LinkedBlockingQueue<>();
    private AtomicInteger caixaCounter = new AtomicInteger(0);
    private java.util.List<Caixa> caixas = new java.util.ArrayList<>();

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
        logScroll.setPreferredSize(new Dimension(1200, 500));
        logScroll.setBorder(BorderFactory.createTitledBorder("Log de Atividades"));
        add(logScroll, BorderLayout.SOUTH);

        // Painel de controles
        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(new Color(240, 240, 240));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Controles"));

        abrirCaixaBtn = new JButton("➕ Abrir Novo Caixa");
        abrirCaixaBtn.setBackground(new Color(100, 200, 100));
        abrirCaixaBtn.setForeground(Color.WHITE);
        abrirCaixaBtn.setFont(new Font("Arial", Font.BOLD, 14));

        controlPanel.add(abrirCaixaBtn);
        add(controlPanel, BorderLayout.WEST);

        // Evento
        abrirCaixaBtn.addActionListener(e -> abrirNovoCaixa());

        // Iniciar simulação com 2 caixas fixos
        iniciarSimulacao();
    }

    private void iniciarSimulacao() {
        logArea.setText("");
        log("🚀 SIMULAÇÃO INICIADA - 2 CAIXAS FIXOS");
        log("📊 Clientes são gerados automaticamente e distribuídos para os caixas");

        // Criar dois caixas iniciais
        abrirNovoCaixa();
        abrirNovoCaixa();
    }

    private void abrirNovoCaixa() {
        int numeroCaixa = caixaCounter.incrementAndGet();
        JPanel caixaPanel = criarPainelCaixa(numeroCaixa);
        caixasPanel.add(caixaPanel);

        Caixa caixa = new Caixa(numeroCaixa, filaClientes, logArea, caixaPanel);
        caixas.add(caixa);
        new Thread(caixa).start();

        log("🛒 NOVO CAIXA ABERTO: Caixa " + numeroCaixa);
        caixasPanel.revalidate();
        caixasPanel.repaint();
    }

    private JPanel criarPainelCaixa(int numeroCaixa) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(150, 150, 150), 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setBackground(new Color(230, 245, 255));
        panel.setPreferredSize(new Dimension(800, 120));

        // Título do caixa
        JLabel tituloCaixa = new JLabel("CAIXA " + numeroCaixa, SwingConstants.CENTER);
        tituloCaixa.setFont(new Font("Arial", Font.BOLD, 16));
        tituloCaixa.setForeground(new Color(0, 80, 150));
        tituloCaixa.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // Painel de status
        JPanel statusPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        statusPanel.setBackground(new Color(230, 245, 255));

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