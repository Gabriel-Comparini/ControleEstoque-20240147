# Para Executar no windows:
```bash
mvnw.cmd spring-boot:run
```
# Rotas:
## CLIENTES

### GET
Seleciona todos os clientes

### GET / 
Seleciona um cliente especifico por id

### POST
```json
{
	"nome": "Gabriel",
	"email": "gc7@email",
	"endereco": "Av. Jorge",
	"telefone": "19989208669"
}
```
Cria um novo cliente

### PUT /
Atualiza os dados de um cliente pelo seu id

### DELETE / 
Deleta um cliente pelo id

## CATEGORIAS

### GET
Seleciona todos as categorias

### GET / 
Seleciona uma categoria especifica por id

### POST
```json
{
	"nome": "Frutas"
}
```
Cria uma nova categoria

### PUT /
Atualiza os dados de uma categoria pelo seu id

### DELETE / 
Deleta uma categoria pelo id (se não houver mais produtos);

## Fornecedores

### GET
Seleciona todos os fornecedores

### GET / 
Seleciona um fornecedor especifico por id

### POST
```json
{
	"nome": "Marcos"
}
```
Cria um novo fornecedor

### PUT /
Atualiza os dados de um fornecedor pelo seu id

### DELETE / 
Deleta um fornecedor pelo id (se não houver produtos)

## PRODUTOS

### GET
Seleciona todos os produtos

### GET / 
Seleciona um produto especifico por id

### POST
```json
{
    {
        "nome": "maça",
        "preco": 12,
        "categoria": {
            "id": 1
        },
        "fornecedores": [
            {
                "id": 1
            }
        ],
        "estoque": {
            "quantidade": 18
        }
    }	
}
```
Cria um novo produto

### PUT /
Atualiza os dados de um produto pelo seu id

### DELETE / 
Deleta um produto pelo id

## VENDAS
### POST 
```json
{  
	"cliente": { 
		"id": 1
	},
  	"itens": [
		{
			"produto": { 
				"id": 1
				},
			"quantidade": 5
		}
  	]
}
```
Realiza a compra/venda do produto, subtraindo de sua quantidade total


# VÍDEO DO PROJETO:
https://youtu.be/5vjMxc0NtH8