package supermercado.model;

import java.util.List;

public class Cliente {
    private final String nome;
    private final List<Produto> produtos;
    private final long tempoChegadaFila;

    public Cliente(String nome, List<Produto> produtos) {
        this(nome, produtos, System.currentTimeMillis());
    }

    public Cliente(String nome, List<Produto> produtos, long tempoChegadaFila) {
        if (nome == null || nome.trim().isEmpty())
            throw new IllegalArgumentException("Nome inválido");
        if (produtos == null || produtos.isEmpty())
            throw new IllegalArgumentException("Produtos não podem ser nulos ou vazios");
        this.nome = nome;
        this.produtos = java.util.Collections.unmodifiableList(new java.util.ArrayList<>(produtos));
        this.tempoChegadaFila = tempoChegadaFila;
    }

    public String getNome() { return nome; }
    public List<Produto> getProdutos() { return produtos; }
    public long getTempoChegadaFila() { return tempoChegadaFila; }
}