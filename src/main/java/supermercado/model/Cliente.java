package supermercado.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Cliente {
    private final String nome;
    private List<Produto> produtos;

    public Cliente(String nome, List<Produto> produtos) {
        if (nome == null || nome.trim().isEmpty())
            throw new IllegalArgumentException("Nome inválido");
        if (produtos == null || produtos.isEmpty())
            throw new IllegalArgumentException("Produtos não podem ser nulos ou vazios");

        this.nome = nome;
        this.produtos = Collections.unmodifiableList(new ArrayList<>(produtos));
    }

    public String getNome() {
        return nome;
    }

    public List<Produto> getProdutos() {
        return produtos;
    }

    @Override
    public String toString() {
        return "👤 Cliente " + nome + " com " + produtos.size() + " produtos";
    }
}