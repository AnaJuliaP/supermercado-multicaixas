package supermercado;

import supermercado.models.Cliente;
import supermercado.models.Produto;
import supermercado.services.ClienteGenerator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class Main {

    private static JTextArea textArea;

    public static void main(String[] args) {
        // Criar janela
        JFrame frame = new JFrame("Supermercado - Clientes");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 500);
        frame.setLayout(new BorderLayout());

        // Área de texto para mostrar clientes
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));

        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Botão para gerar clientes aleatórios
        JButton btnGerar = new JButton("Gerar Clientes Aleatórios");
        btnGerar.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        frame.add(btnGerar, BorderLayout.SOUTH);

        // Ação do botão
        btnGerar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gerarClientes();
            }
        });

        // Mostrar clientes iniciais
        gerarClientes();

        // Mostrar janela
        frame.setVisible(true);
    }

    private static void gerarClientes() {
        List<Cliente> clientesFixos = ClienteGenerator.gerarClientesFixos();
        List<Cliente> clientesAleatorios = ClienteGenerator.gerarClientesAleatorios(5);

        StringBuilder sb = new StringBuilder();
        sb.append("=== Clientes Fixos ===\n");
        for (Cliente c : clientesFixos) {
            sb.append(c).append("\n");
            for (Produto p : c.getProdutos()) {
                sb.append("   ").append(p).append("\n");
            }
        }

        sb.append("\n=== Clientes Aleatórios ===\n");
        for (Cliente c : clientesAleatorios) {
            sb.append(c).append("\n");
            for (Produto p : c.getProdutos()) {
                sb.append("   ").append(p).append("\n");
            }
        }

        textArea.setText(sb.toString());
    }
}
