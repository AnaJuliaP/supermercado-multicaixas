package supermercado.services;

import supermercado.models.Cliente;
import supermercado.models.Produto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ClienteGenerator {
    private static final Random random = new Random();

    private static final String[] NOMES_CLIENTES = {
            "Ana", "Bruno", "Carla", "Diego", "Eduarda", "Felipe", "Giovana", "Henrique"
    };

    private static final Produto[] PRODUTOS_BASE = {
            new Produto("🥛 Leite", 1000),
            new Produto("🍞 Pão", 1500),
            new Produto("🍫 Chocolate", 2000),
            new Produto("🍎 Maçã", 800),
            new Produto("🥩 Carne", 2500),
            new Produto("🍪 Biscoito", 1200)
    };

    public static List<Cliente> gerarClientesFixos() {
        List<Cliente> clientes = new ArrayList<>();

        clientes.add(new Cliente("Ana", new ArrayList<>(Arrays.asList(
                PRODUTOS_BASE[0], PRODUTOS_BASE[1], PRODUTOS_BASE[2]
        ))));
        clientes.add(new Cliente("Bruno", new ArrayList<>(Arrays.asList(
                PRODUTOS_BASE[3], PRODUTOS_BASE[4]
        ))));
        clientes.add(new Cliente("Carla", new ArrayList<>(Arrays.asList(
                PRODUTOS_BASE[5], PRODUTOS_BASE[0], PRODUTOS_BASE[1]
        ))));

        return clientes;
    }

    public static List<Cliente> gerarClientesAleatorios(int quantidade) {
        List<Cliente> clientes = new ArrayList<>();

        for (int i = 0; i < quantidade; i++) {
            String nome = NOMES_CLIENTES[random.nextInt(NOMES_CLIENTES.length)];
            int qtdProdutos = 1 + random.nextInt(5); 

            List<Produto> produtos = new ArrayList<>();
            for (int j = 0; j < qtdProdutos; j++) {
                produtos.add(PRODUTOS_BASE[random.nextInt(PRODUTOS_BASE.length)]);
            }

            clientes.add(new Cliente(nome, produtos));
        }

        return clientes;
    }
}
