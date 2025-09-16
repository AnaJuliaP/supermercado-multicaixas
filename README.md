# 🛒 Simulação de Caixa de Supermercado

## 📖 Descrição
Este projeto simula o funcionamento de um supermercado com **múltiplos caixas atendendo clientes em paralelo**, utilizando **threads** para demonstrar o comportamento de filas simultâneas.

Cada **caixa** funciona como uma thread independente, processando clientes da fila de espera em tempo real.

A interface gráfica permite:
- ✅ Abrir novos caixas (dinâmicos) ou manter caixas fixos.
- ✅ Remover caixas dinâmicos.
- ✅ Visualizar o status de cada caixa e o progresso de atendimento.
- ✅ Monitorar a fila de clientes e o log de atividades do supermercado.

---

## ⚡ Funcionalidades
- Simulação de **atendimento paralelo** em caixas.
- Dois **caixas fixos iniciais**, que não podem ser removidos.
- Abertura de novos **caixas dinâmicos** (até um limite definido).
- **Atualização visual em tempo real** da fila e do status dos caixas.
- **Registro em log** das ações: chegada de clientes, abertura e remoção de caixas.

---

## 🛠 Tecnologias
- **Java (Swing)** → Interface gráfica.
- **Threads** → Atendimento paralelo.
- **BlockingQueue** → Gerenciamento seguro da fila entre threads.

---

## 🚀 Como usar
1. Compile e execute a classe **`Main.java`**.
2. A janela da simulação abrirá com **2 caixas fixos** já operando.
3. Use os botões para:
    - ➕ Abrir novos caixas dinâmicos.
    - ➖ Remover caixas dinâmicos.
4. Observe a distribuição de clientes e acompanhe o **log de atividades**.

---
## 📌 Observações
Este projeto tem foco **educacional**, demonstrando conceitos de:
- Programação concorrente.
- Gerenciamento de filas.
- Sincronização de threads com recursos compartilhados.  
