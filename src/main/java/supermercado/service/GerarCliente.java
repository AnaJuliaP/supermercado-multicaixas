package supermercado.service;

import supermercado.model.Cliente;
import supermercado.model.Produto;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GerarCliente {
    private static final Random random = new Random();

    private static final String[] NOMES_CLIENTES = {
            "Ana", "Bruno", "Carla", "Diego", "Eduarda", "Felipe", "Giovana", "Henrique",
            "Isabela", "João", "Katia", "Lucas", "Maria", "Nuno", "Olívia", "Paulo",
            "Queila", "Ricardo", "Sofia", "Tiago", "Úrsula", "Vitor", "Wagner", "Xavier", "Yara", "Zé"
    };

    private static final Produto[] PRODUTOS_BASE = {
            new Produto("🥛 Leite", 1000),
            new Produto("🍞 Pão", 800),
            new Produto("🍫 Chocolate", 1200),
            new Produto("🍎 Maçã", 500),
            new Produto("🥩 Carne", 2000),
            new Produto("🍪 Biscoito", 900),
            new Produto("🍚 Arroz", 1500),
            new Produto("🍝 Macarrão", 1300),
            new Produto("🍅 Tomate", 600),
            new Produto("🧀 Queijo", 1100),
            new Produto("🍗 Frango", 1800),
            new Produto("🍌 Banana", 400),
            new Produto("🍇 Uva", 700),
            new Produto("🍊 Laranja", 550),
            new Produto("🥕 Cenoura", 650),
            new Produto("🥔 Batata", 750),
            new Produto("🌽 Milho", 950),
            new Produto("🍋 Limão", 450),
            new Produto("🍉 Melancia", 1600),
            new Produto("🍍 Abacaxi", 1400),
            new Produto("🍓 Morango", 850),
            new Produto("🥒 Pepino", 600),
            new Produto("🥬 Alface", 500),
            new Produto("🍆 Berinjela", 900),
            new Produto("🌶️ Pimentão", 800),
            new Produto("🍄 Cogumelo", 1100)
    };

    public static List<Cliente> gerarClientesAleatorios(int quantidade) {
        List<Cliente> clientes = new ArrayList<>();

        for (int i = 0; i < quantidade; i++) {
            String nome = NOMES_CLIENTES[random.nextInt(NOMES_CLIENTES.length)];

            // cada cliente leva entre 1 e 8 produtos
            int qtdProdutos = 1 + random.nextInt(8);
            List<Produto> produtos = new ArrayList<>();

            for (int j = 0; j < qtdProdutos; j++) {
                Produto produto = PRODUTOS_BASE[random.nextInt(PRODUTOS_BASE.length)];
                produtos.add(produto);
            }

            clientes.add(new Cliente(nome, produtos, System.currentTimeMillis()));

        }

        return clientes;
    }

}