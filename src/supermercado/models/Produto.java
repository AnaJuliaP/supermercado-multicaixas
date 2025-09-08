package supermercado.models;

public class Produto {
    private final String nome;           
    private final int tempoProcessamento; 

    public Produto(String nome, int tempoProcessamento) {
        if (nome == null || nome.trim().isEmpty()) 
            throw new IllegalArgumentException("Nome inválido");
        if (tempoProcessamento < 0) 
            throw new IllegalArgumentException("Tempo não pode ser negativo");
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
        return nome + " - " + String.format("%.1fs", tempoProcessamento / 1000.0);
    }
}
