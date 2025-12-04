package com.controleestoque.api_estoque.controller;
import java.util.List;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import com.controleestoque.api_estoque.model.Clientes;
import com.controleestoque.api_estoque.repository.ClientesRepository;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClientesController {
    private final ClientesRepository clientesRepository; 

    @GetMapping
    public List<Clientes> getAllFornecedores() {
        return clientesRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Clientes> getCategoriaById(@PathVariable Long id) {
        return clientesRepository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Clientes createClientes(@RequestBody Clientes clientes) {
        return clientesRepository.save(clientes);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Clientes> updateClientes(@PathVariable Long id, @RequestBody Clientes clientesDetails){
        return clientesRepository.findById(id).map(clientes -> {
            clientes.setNome(clientesDetails.getNome());
            clientes.setEmail(clientesDetails.getEmail());
            clientes.setEndereco(clientesDetails.getEndereco());
            clientes.setTelefone(clientesDetails.getTelefone());
            Clientes updatedClientes = clientesRepository.save(clientes);
            return ResponseEntity.ok(updatedClientes);
        }).orElse(ResponseEntity.notFound().build());
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClientes(@PathVariable Long id){
        if(!clientesRepository.existsById(id)){
            return ResponseEntity.notFound().build();
        }
        clientesRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
