package com.controleestoque.api_estoque.controller;
import java.util.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import com.controleestoque.api_estoque.model.Fornecedor;
import com.controleestoque.api_estoque.model.ItensVendas;
import com.controleestoque.api_estoque.model.Produto;
import com.controleestoque.api_estoque.model.Estoque;
import com.controleestoque.api_estoque.repository.*;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/produtos")
@RequiredArgsConstructor
public class ProdutoController {
    private final ProdutoRepository produtoRepository;
    private final FornecedorRepository fornecedorRepository;
    private final CategoriaRepository categoriaRepository;
    private final ItensVendasRepository itensVendasRepository;

    @GetMapping
    public List<Produto> getAllProdutos() {
        return produtoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produto> getProdutoById(@PathVariable Long id) {
        return produtoRepository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Produto> createProduto(@RequestBody Produto produto) {

        if (!validarCategoria(produto)) {
            return ResponseEntity.badRequest().build();
        }

        associarCategoria(produto);
        associarFornecedores(produto);
        associarEstoque(produto);

        Produto saved = produtoRepository.save(produto);

        atualizarRelacionamentoFornecedores(saved);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Produto> updateProduto(@PathVariable Long id, @RequestBody Produto details) {

        return produtoRepository.findById(id).map(produto -> {

            atualizarCamposSimples(produto, details);
            atualizarCategoria(produto, details);
            atualizarFornecedores(produto, details);
            atualizarEstoque(produto, details);

            Produto updated = produtoRepository.save(produto);

            sincronizarFornecedoresAtualizados(produto);

            return ResponseEntity.ok(updated);

        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduto(@PathVariable Long id) {
        Produto produto = produtoRepository.findById(id).orElse(null);
        if (produto == null) {
            return ResponseEntity.notFound().build();
        }

        List<ItensVendas> itens = itensVendasRepository.findByProdutoId(id);
        for (ItensVendas item : itens) {
            itensVendasRepository.delete(item);
        }

        if (produto.getEstoque() != null) {
            produto.getEstoque().setProduto(null);
            produto.setEstoque(null);
        }

        if (produto.getFornecedores() != null) {
            produto.getFornecedores().forEach(f -> f.getProdutos().remove(produto));
            produto.getFornecedores().clear();
        }

        produtoRepository.delete(produto);

        return ResponseEntity.noContent().build();
    }



    private boolean validarCategoria(Produto produto) {
        return produto.getCategoria() != null && produto.getCategoria().getId() != null;
    }

    private void associarCategoria(Produto produto) {
        categoriaRepository.findById(produto.getCategoria().getId()).ifPresent(produto::setCategoria);
    }

    private void atualizarCategoria(Produto produto, Produto details) {
        if (details.getCategoria() != null && details.getCategoria().getId() != null) {
            categoriaRepository.findById(details.getCategoria().getId()).ifPresent(produto::setCategoria);
        }
    }

    private void associarFornecedores(Produto produto) {
        Set<Fornecedor> fornecedores = new HashSet<>();

        if (produto.getFornecedores() != null) {
            produto.getFornecedores().forEach(f -> {
                if (f != null && f.getId() != null) {
                    fornecedorRepository.findById(f.getId()).ifPresent(fornecedores::add);
                }
            });
        }

        produto.setFornecedores(fornecedores);
    }

    private void atualizarFornecedores(Produto produto, Produto details) {

        if (produto.getFornecedores() != null) {
            produto.getFornecedores().forEach(f -> f.removeProduto(produto));
            produto.getFornecedores().clear();
        }

        if (details.getFornecedores() != null) {
            details.getFornecedores().forEach(req -> {
                if (req != null && req.getId() != null) {
                    fornecedorRepository.findById(req.getId()).ifPresent(f -> f.addProduto(produto));
                }
            });
        }
    }

    private void atualizarRelacionamentoFornecedores(Produto saved) {
        saved.getFornecedores().forEach(f -> {
            f.getProdutos().add(saved);
            fornecedorRepository.save(f);
        });
    }

    private void sincronizarFornecedoresAtualizados(Produto produto) {
        produto.getFornecedores().forEach(fornecedorRepository::save);
    }

    private void associarEstoque(Produto produto) {
        if (produto.getEstoque() != null) {
            produto.getEstoque().setProduto(produto);
        }
    }

    private void atualizarEstoque(Produto produto, Produto details) {
        Estoque estoqueReq = details.getEstoque();
        if (estoqueReq != null && produto.getEstoque() != null) {
            produto.getEstoque().setQuantidade(estoqueReq.getQuantidade());
        }
    }

    private void atualizarCamposSimples(Produto produto, Produto details) {
        produto.setNome(details.getNome());
        produto.setPreco(details.getPreco());
    }
}
