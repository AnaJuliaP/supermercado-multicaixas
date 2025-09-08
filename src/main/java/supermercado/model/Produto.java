package supermercado.model;

public class Produto {
    private final String nome;
    private final int tempoProcessamento;

    public Produto(String nome, int tempoProcessamento) {
        this.nome = nome;
        this.tempoProcessamento = tempoProcessamento;
    }

    public String getNome() {
        return nome;
    }

    public int getTempoProcessamento() {
        return tempoProcessamento;
    }

    @Override
    public String toString() {
        return nome + " (⏱ " + tempoProcessamento + "ms)";
    }
}