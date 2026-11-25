@AGENTS.md

## Claude

- Seguir o `AGENTS.md` acima; ele é a fonte das regras do projeto.
- Respostas curtas e diretas, em português.
- Explicar decisões de arquitetura quando criar uma camada ou entidade nova.
- Arquitetura em camadas (não pensar em MVC): os endpoints ficam no pacote `web`; a camada de apresentação é `web` + `dto` + `templates` + `static`.
- `config` → configuração de infraestrutura (segurança, log e afins).
