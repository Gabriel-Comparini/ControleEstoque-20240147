package com.controleestoque.api_estoque.controller;
import java.math.BigDecimal;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import com.controleestoque.api_estoque.model.*;
import com.controleestoque.api_estoque.repository.ClientesRepository;
import com.controleestoque.api_estoque.repository.ItensVendasRepository;
import com.controleestoque.api_estoque.repository.ProdutoRepository;
import com.controleestoque.api_estoque.repository.VendasRepository;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/vendas")
@RequiredArgsConstructor
public class VendaController {
    private final VendasRepository vendaRepository;
    private final ClientesRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final ItensVendasRepository itemVendaRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> createVenda(@RequestBody Venda venda) {

        Clientes cliente = validarCliente(venda);
        if (cliente == null) {
            return erro("Não achei clientes.");
        }

        venda.setCliente(cliente);

        BigDecimal valorTotal = processarItensDaVenda(venda);
        if (valorTotal == null) {
            return erro("Não tenho itens o suficiente.");
        }

        venda.setValorTotal(valorTotal);

        Venda vendaSalva = vendaRepository.save(venda);

        for (ItensVendas item : venda.getItens()) {
            item.setVenda(vendaSalva);
            itemVendaRepository.save(item);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(vendaRepository.findById(vendaSalva.getId()).get());
    }

    private Clientes validarCliente(Venda venda) {
        Long clienteId = venda.getCliente() != null ? venda.getCliente().getId() : null;
        return clienteId == null ? null : clienteRepository.findById(clienteId).orElse(null);
    }

    private BigDecimal processarItensDaVenda(Venda venda) {

        BigDecimal total = BigDecimal.ZERO;

        for (ItensVendas item : venda.getItens()) {

            Produto produto = produtoRepository.findById(item.getProduto().getId()).orElse(null);

            if (produto == null) {
                return null;
            }

            if (!estoqueSuficiente(produto, item)) {
                return null;
            }

            atualizarEstoque(produto, item);

            prepararItem(item, produto);

            BigDecimal subtotal = item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade()));

            total = total.add(subtotal);
        }

        return total;
    }

    private boolean estoqueSuficiente(Produto produto, ItensVendas item) {
        return produto.getEstoque().getQuantidade() >= item.getQuantidade();
    }

    private void atualizarEstoque(Produto produto, ItensVendas item) {
        Estoque e = produto.getEstoque();
        e.setQuantidade(e.getQuantidade() - item.getQuantidade());
        produto.setEstoque(e);
        produtoRepository.save(produto);
    }

    private void prepararItem(ItensVendas item, Produto produto) {
        item.setProduto(produto);
        item.setPrecoUnitario(produto.getPreco());
    }

    private ResponseEntity<String> erro(String msg) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
    }
}