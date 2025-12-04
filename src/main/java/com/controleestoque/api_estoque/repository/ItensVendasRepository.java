package com.controleestoque.api_estoque.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.controleestoque.api_estoque.model.ItensVendas;

@Repository
public interface ItensVendasRepository extends JpaRepository<ItensVendas, Long> {
    // Valeu pela ajuda Breno S2
    boolean existsByProdutoId(Long produtoId);
    List<ItensVendas> findByProdutoId(Long produtoId);
}