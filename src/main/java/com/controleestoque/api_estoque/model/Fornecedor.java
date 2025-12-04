package com.controleestoque.api_estoque.model;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Set;

@Entity
@Table(name = "tb_fornecedores")
public class Fornecedor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nome;
    private boolean estaAVenda = true;

    @ManyToMany(mappedBy = "fornecedores", fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<Produto> produtos;

    public Fornecedor() {}

    public Fornecedor(String nome, Set<Produto> produtos) {
        this.nome = nome;
        this.produtos = produtos;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Set<Produto> getProdutos() {
        return produtos;
    }

    public void setProdutos(Set<Produto> produtos) {
        this.produtos = produtos;
    }

    public void addProduto(Produto produto) {
        this.produtos.add(produto);
        produto.getFornecedores().add(this);
    }

    public void removeProduto(Produto produto) {
        this.produtos.remove(produto);
        produto.getFornecedores().remove(this);
    }

    public boolean isestaAVenda() {
        return estaAVenda;
    }

    public void setestaAVenda(boolean estaAVenda) {
        this.estaAVenda = estaAVenda;
    }
    
} 