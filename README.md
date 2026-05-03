# busca-cep

## O que é este projeto?

O **busca-cep** é um serviço web simples escrito em Clojure que permite consultar informações de CEP (Código de Endereçamento Postal) brasileiro. Ele utiliza a API pública do ViaCEP para buscar os dados e armazena em cache no DynamoDB para melhorar a performance e reduzir chamadas externas.

Este projeto foi desenvolvido seguindo a **Arquitetura Hexagonal** (também conhecida como Ports and Adapters), que separa a lógica de negócio das interfaces externas, tornando o código mais modular e fácil de testar.

### Objetivo
- Fornecer uma API REST para consulta de CEPs.
- Demonstrar boas práticas de arquitetura em Clojure.
- Ser um exemplo educacional para iniciantes em desenvolvimento web com Clojure.

## Estrutura do Projeto

O projeto está organizado da seguinte forma:

- `src/busca_cep/`: Código fonte principal
  - `core.clj`: Função principal para consultar CEP.
  - `main.clj`: Ponto de entrada da aplicação.
  - `system.clj`: Configuração e inicialização do sistema.
  - `config.edn`: Arquivo de configuração (usado pelo Integrant).
  - `adapters/`: Adaptadores para interfaces externas
    - `via_cep.clj`: Adaptador para a API ViaCEP.
    - `dynamo_cache.clj`: Adaptador para cache no DynamoDB.
  - `domain/`: Lógica de domínio
    - `cep.clj`: Modelo de dados do CEP.
  - `http/`: Camada de apresentação HTTP
    - `router.clj`: Definição das rotas da API.
    - `server.clj`: Inicialização do servidor HTTP.
  - `infra/`: Infraestrutura
    - `dynamo.clj`: Cliente para DynamoDB.
  - `ports/`: Interfaces (Ports) da arquitetura hexagonal
    - `cep_port.clj`: Porta para operações de CEP.

- `test/`: Testes automatizados.
- `project.clj`: Arquivo de configuração do Leiningen (gerenciador de dependências e build).
- `target/`: Arquivos gerados durante o build.

## Como rodar o projeto

### Pré-requisitos
- **Java 8 ou superior**: O Clojure roda na JVM.
- **Leiningen**: Ferramenta para gerenciar projetos Clojure. Instale seguindo as instruções em [leiningen.org](https://leiningen.org/).
- **LocalStack**: Para simular o DynamoDB localmente (opcional, mas recomendado para desenvolvimento). Instale via Docker: `docker run -d -p 4566:4566 localstack/localstack`.

### Passos para executar
1. **Clone ou baixe o projeto**:
   ```
   git clone <url-do-repositorio>
   cd busca-cep
   ```

2. **Instale as dependências**:
   ```
   lein deps
   ```
2.1 **Configure os Exports**:
   ```
export AWS_ACCESS_KEY_ID=test
export AWS_SECRET_ACCESS_KEY=test
export AWS_DEFAULT_REGION=us-east-1

aws --endpoint-url=http://localhost:4566 dynamodb create-table \  
  --table-name cep \             
  --attribute-definitions AttributeName=cep,AttributeType=S \
  --key-schema AttributeName=cep,KeyType=HASH \
  --billing-mode PAY_PER_REQUEST
   ```

3. **Inicie o LocalStack** (para DynamoDB local):
   ```
   docker run -d -p 4566:4566 --name localstack localstack/localstack
   ```
   Isso iniciará o LocalStack na porta 4566.

   **Verifique se o LocalStack está funcionando** (health check do ambiente):
   ```
   curl http://localhost:4566/_localstack/health
   ```
   Resposta esperada (indica que o DynamoDB local está pronto):
   ```json
   {"dynamodb": "available"}
   ```

4. **Configure as variáveis de ambiente** (se necessário):
   - O projeto usa LocalStack por padrão. Se quiser usar AWS real, configure `AWS_ACCESS_KEY_ID` e `AWS_SECRET_ACCESS_KEY`.

5. **Execute o projeto**:
   ```
   lein run
   ```
   Você verá a mensagem: "Sistema iniciado com DynamoDB (LocalStack) + ViaCEP cacheado!" e "Servidor rodando em http://localhost:3000".

6. **Pare o projeto**: Pressione `Ctrl+C` no terminal.

### Para produção
Para gerar um JAR executável:
```
lein uberjar
java -jar target/uberjar/busca-cep-0.1.0-SNAPSHOT-standalone.jar
```

## Exemplos de uso

Use o `curl` ou qualquer cliente HTTP para testar a API.

### Verificar se o serviço está funcionando (Health Check)
```
curl http://localhost:3000/status
```
Resposta esperada:
```json
{"status": "ok"}
```

### Consultar um CEP válido (HTTP REST)
```
curl http://localhost:3000/cep/01001000
```
Resposta esperada (exemplo):
```json
{
  "cep": "01001-000",
  "logradouro": "Praça da Sé",
  "complemento": "lado ímpar",
  "bairro": "Sé",
  "localidade": "São Paulo",
  "uf": "SP",
  "ibge": "3550308",
  "gia": "1004",
  "ddd": "11",
  "siafi": "7107"
}
```

### Consultar um CEP inválido
```
curl http://localhost:3000/cep/00000000
```
Resposta esperada (erro):
```json
{
  "erro": true
}
```

### Consultar um CEP com formato incorreto
```
curl http://localhost:3000/cep/abc
```
Resposta esperada (erro, pois espera apenas números):
```json
{}
```

### Webhook para integração
Envie um POST com JSON `{"cep": "01001000"}` para `http://localhost:3000/webhook/cep`:

```bash
curl -X POST \
  -H "Content-Type: application/json" \
  -d '{"cep": "01001000"}' \
  http://localhost:3000/webhook/cep
```

- Substitua o CEP nos exemplos por qualquer valor válido (apenas números, 8 dígitos).
- O cache em DynamoDB acelera consultas repetidas do mesmo CEP.
- A API usa a Arquitetura Hexagonal: HTTP REST e Webhooks compartilham o mesmo cache e lógica de negócio.

## Testes

O projeto inclui testes automatizados para garantir a qualidade do código. Todos os testes estão passando e a cobertura de código é de 100%.

### Executar os testes de unidade
```
lein test
```

### Ver a cobertura dos testes
```
lein cloverage
```
Isso gera um relatório HTML em `target/coverage/index.html` com detalhes da cobertura.

## Contribuição

Sinta-se à vontade para abrir issues ou pull requests no repositório. Este projeto é um exemplo educacional, então sugestões de melhorias são bem-vindas!

## Licença

Copyright © 2025

Este programa e os materiais que o acompanham são disponibilizados sob os termos da Licença Pública Eclipse 2.0, disponível em http://www.eclipse.org/legal/epl-2.0.

Este Código Fonte também pode ser disponibilizado sob as seguintes Licenças Secundárias quando as condições para tal disponibilidade estabelecidas na Licença Pública Eclipse, v. 2.0 forem satisfeitas: Licença Pública Geral GNU conforme publicada pela Free Software Foundation, seja a versão 2 da Licença ou (à sua opção) qualquer versão posterior, com a Exceção da GNU Classpath, disponível em https://www.gnu.org/software/classpath/license.html.
