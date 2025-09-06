package supermercado.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Cliente {
    private final String nome;  
    private final List<Produto> produtos; 

    public Cliente(String nome, List<Produto> produtos) {
        if (nome == null || nome.trim().isEmpty()) 
            throw new IllegalArgumentException("Nome inválido");
        if (produtos == null) 
            throw new IllegalArgumentException("Produtos não podem ser nulos");

        this.nome = nome;
        this.produtos = Collections.unmodifiableList(new ArrayList<>(produtos));
    }

    public String getNome() {
        return nome;
    }

    public List<Produto> getProdutos() {
        return produtos;
    }

    public int getTotalProdutos() {
        return produtos.size();
    }

    public int getTempoTotalProcessamento() {
        int total = 0;
        for (Produto p : produtos) total += p.getTempoProcessamento();
        return total;
    }

    @Override
    public String toString() {
        return "👤 Cliente " + nome + " tem " + getTotalProdutos() + " produtos";
    }
}
