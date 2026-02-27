# OWASP Top 10 - Cobertura do Projeto

## As recomendações foram feitas com base no projeto rbac anterior 

## 1) Broken Access Control
- **SecurityConfig**
  - Na linha 15, habilitamos a segurança com `@EnableMethodSecurity`
  - Entre as linhas 20 e 26, temos a liberação de endpoints públicos, porém os demais são bloqueados
- **ProductController**
  - Usamos diversas anotações para restringir o acesso como `@PermitAll`, `@PreAuthorize`, `@PostAuthorize`, etc.
- **UserController**
  - Não possui anotações, porém está protegido pela regra `anyRequest().authenticated()` do SecurityConfig

---

## 2) Cryptographic Failures
- **SecurityConfig**
  - Na linha 39, definimos `BCryptPasswordEncoder` para criptografar senhas
- **UserService**
  - Na linha 23, codificamos a senha antes de salvar no banco usando `passwordEncoder.encode()`
- **DataInitializer**
  - Senhas dos usuários criados são automaticamente criptografadas pelo UserService

---

## 3) Injection
- **ProductRepository, UserRepository, ProfileRepository, RoleRepository**
  - Uso de JPA/Hibernate com queries parametrizadas, prevenindo SQL Injection
- **Todos os Controllers**
  - Não há concatenação de strings em queries SQL ou uso de SQL nativo
  - Parâmetros são passados via `@PathVariable` e `@RequestBody` e tratados pelo Spring Data JPA

---

## 4) Insecure Design
- **Modelo de Dados (User, Profile, Role)**
  - Implementação de RBAC (Role-Based Access Control) com separação de perfis e permissões
  - Usuários possuem perfis, perfis possuem roles (hierarquia clara)
- **Separação de Camadas**
  - Controllers, Services e Repositories seguem padrão MVC
  - Responsabilidades bem definidas e isoladas

---

## 5) Security Misconfiguration
- **SecurityConfig**
  - Spring Security habilitado e configurado explicitamente
  - CSRF desabilitado conscientemente para APIs REST (linha 32)
  - Headers configurados para permitir H2 console com segurança (linha 33)
- **application.properties**
  - H2 console habilitado mas protegido por autenticação
  - Configurações explícitas de datasource e JPA

---

## 6) Vulnerable and Outdated Components
- **pom.xml**
  - Spring Boot 3.1.6 (versão relativamente recente)
  - Dependências gerenciadas pelo Spring Boot Starter Parent
  - Lombok 1.18.28 (versão atual)
  - Uso de dependências oficiais do ecossistema Spring

---

## 7) Identification and Authentication Failures
- **SecurityConfig**
  - Autenticação HTTP Basic configurada (linha 29)
  - Require autenticação para qualquer requisição não explicitamente liberada
- **CustomUserDetailsService**
  - Implementa `UserDetailsService` para carregar usuários do banco
  - Valida usuários e carrega suas permissões (linhas 20-44)
- **UserService**
  - Senhas são criptografadas com BCrypt antes de salvar
- **DataInitializer**
  - Usuários criados com senhas que serão automaticamente hasheadas

---

## 8) Software and Data Integrity Failures
- **pom.xml**
  - Dependências baixadas de repositórios confiáveis (Maven Central)
  - Versões fixas de dependências definidas pelo Spring Boot Parent
  - Uso de checksums automáticos do Maven
- **Não há**
  - Serialização/deserialização de objetos não confiáveis
  - Updates automáticos sem validação

---

## 9) Security Logging and Monitoring Failures
- **application.properties**
  - `spring.jpa.show-sql=true` na linha 12 para logs de queries SQL
- **Spring Security**
  - Logs automáticos de falhas de autenticação (comportamento padrão)
- **⚠️ Limitações**
  - Não há logging explícito de eventos de segurança (tentativas de acesso negado, mudanças de permissão)
  - Não há monitoramento de comportamento anômalo

---

## 10) Server-Side Request Forgery (SSRF)
- **Todos os Controllers**
  - Não há endpoints que aceitam URLs como parâmetro
  - Não há código que faz requisições HTTP para recursos externos baseado em input do usuário
- **⚠️ Nota**
  - Projeto não possui funcionalidades que tipicamente sofrem de SSRF
  - Se adicionado no futuro, validar e filtrar URLs fornecidas por usuários

---

# 🔧 MELHORIAS SUGERIDAS

## 1) Broken Access Control
- [ ] **UserController**: Adicionar `@PreAuthorize("hasRole('ADMIN')")` nos endpoints de criação e listagem de usuários
  - *Por quê?* Atualmente qualquer usuário autenticado pode criar/listar outros usuários, violando o princípio de menor privilégio.
  - Apenas administradores devem gerenciar contas de usuários para evitar escalação de privilégios.

- [ ] **Validação de Ownership**: Implementar verificação para que usuários só possam editar seus próprios dados
  - *Por quê?* Sem validação de ownership, um usuário pode modificar dados de outros usuários usando IDs diferentes.
  - Previne IDOR (Insecure Direct Object Reference), uma das principais causas de quebra de controle de acesso.

- [ ] **Rate Limiting**: Adicionar limite de tentativas para prevenir brute force
  - *Por quê?* Sem rate limiting, atacantes podem tentar milhares de senhas em segundos.
  - Protege contra ataques de força bruta e enumeration de usuários válidos.

## 2) Cryptographic Failures
- [ ] **Dados Sensíveis**: Implementar criptografia para dados sensíveis em repouso (ex: `@ColumnTransformer` do Hibernate)
  - *Por quê?* Se o banco de dados for comprometido, dados como CPF, cartão de crédito ficam expostos em texto claro.
  - Criptografia em repouso garante que mesmo com acesso ao DB, os dados permanecem protegidos.

- [ ] **HTTPS**: Configurar redirect automático de HTTP para HTTPS em produção
  - *Por quê?* HTTP trafega dados em texto claro, permitindo interceptação via MITM (Man-in-the-Middle).
  - HTTPS criptografa toda comunicação, protegendo credenciais e tokens em trânsito.

- [ ] **Secrets Management**: Usar Spring Cloud Config ou Vault para gerenciar credenciais ao invés de application.properties
  - *Por quê?* Secrets em arquivos de configuração podem vazar para repositórios Git ou logs.
  - Ferramentas especializadas oferecem rotação automática, auditoria e controle de acesso granular.

## 3) Injection
- [ ] **Validação de Input**: Adicionar `@Valid` e criar DTOs com `@NotNull`, `@Size`, `@Pattern` para validar inputs
  - *Por quê?* Inputs não validados podem conter payloads maliciosos (SQL, scripts, comandos).
  - Validação no backend é a última linha de defesa, pois validação frontend pode ser bypassada.

- [ ] **Sanitização**: Implementar sanitização de HTML em campos de texto livre
  - *Por quê?* Campos de texto podem receber scripts maliciosos que executam no navegador de outros usuários (XSS).
  - Sanitização remove ou escapa tags perigosas, prevenindo execução de código não autorizado.

- [ ] **Query Validation**: Adicionar validação explícita em queries customizadas (se houver)
  - *Por quê?* Queries nativas ou JPQL com concatenação de strings podem permitir SQL Injection.
  - Usar parâmetros nomeados e validação evita que atacantes manipulem a lógica da query.

## 4) Insecure Design
- [ ] **DTOs**: Criar DTOs separados para Request/Response ao invés de expor entidades diretamente
  - *Por quê?* Entidades JPA expõem toda a estrutura do banco (relações, IDs internos, campos sensíveis).
  - DTOs permitem controlar exatamente quais dados são enviados/recebidos, evitando mass assignment e vazamento de dados.

- [ ] **Princípio do Menor Privilégio**: Revisar permissões granulares (ex: separar `PRODUCT_CREATE`, `PRODUCT_UPDATE`, `PRODUCT_DELETE`)
  - *Por quê?* Permissões amplas como "ADMIN" dão acesso total, dificultando controle fino.
  - Permissões granulares permitem dar apenas os acessos necessários, reduzindo impacto de comprometimento.

- [ ] **Auditoria**: Adicionar campos de auditoria (createdBy, createdAt, updatedBy, updatedAt) nas entidades
  - *Por quê?* Sem auditoria, é impossível rastrear quem fez alterações ou quando dados foram modificados.
  - Fundamental para investigações de incidentes, conformidade regulatória e detecção de atividades suspeitas.

## 5) Security Misconfiguration
- [ ] **Perfis de Ambiente**: Criar `application-dev.properties` e `application-prod.properties` separados
  - *Por quê?* Configurações de desenvolvimento (logs verbosos, ferramentas debug) não devem ir para produção.
  - Separação por ambiente reduz o risco de expor informações sensíveis ou funcionalidades inseguras.

- [ ] **H2 Console**: Desabilitar H2 console em produção usando profiles
  - *Por quê?* H2 Console permite acesso direto ao banco de dados, expondo TODOS os dados.
  - Útil apenas em desenvolvimento; em produção é uma porta aberta para atacantes.

- [ ] **CORS**: Configurar CORS explicitamente com origins permitidos
  - *Por quê?* CORS mal configurado permite que sites maliciosos façam requisições em nome de usuários autenticados.
  - Whitelist de origins previne ataques cross-origin e controla quais domínios podem consumir a API.

- [ ] **Security Headers**: Adicionar headers de segurança (X-Content-Type-Options, X-Frame-Options, CSP)
  - *Por quê?* Headers de segurança instruem o navegador a aplicar proteções adicionais contra XSS, clickjacking e MIME sniffing.
  - São defesas em camadas que funcionam mesmo se outras proteções falharem.

## 6) Vulnerable and Outdated Components
- [ ] **Dependency Check**: Integrar OWASP Dependency Check Plugin no pom.xml
  - *Por quê?* Vulnerabilidades em dependências são descobertas constantemente (Log4Shell, Spring4Shell).
  - Scan automatizado detecta CVEs conhecidos antes de chegarem à produção.

- [ ] **Spring Boot**: Atualizar para versão mais recente 3.x
  - *Por quê?* Versões antigas não recebem patches de segurança, deixando vulnerabilidades conhecidas abertas.
  - Atualizações incluem correções críticas e melhorias de segurança acumuladas.

- [ ] **Renovate/Dependabot**: Configurar atualizações automáticas de dependências
  - *Por quê?* Atualização manual é fácil de esquecer e gera débito técnico.
  - Automação garante que patches de segurança sejam aplicados rapidamente.

- [ ] **Scan Regular**: Executar `mvn dependency:tree` e `mvn versions:display-dependency-updates` regularmente
  - *Por quê?* Dependências transitivas podem ter vulnerabilidades não detectadas diretamente.
  - Visibilidade completa da árvore de dependências ajuda a identificar riscos ocultos.

## 7) Identification and Authentication Failures
- [ ] **JWT**: Migrar de HTTP Basic para JWT tokens para APIs stateless
  - *Por quê?* HTTP Basic envia credenciais em cada requisição, aumentando superfície de ataque.
  - JWT permite autenticação stateless, escalabilidade horizontal e expiração automática de tokens.

- [ ] **Política de Senhas**: Implementar validação de força de senha (mínimo 8 caracteres, caracteres especiais, etc)
  - *Por quê?* Senhas fracas são quebradas em segundos por ferramentas de cracking.
  - Política forte força usuários a criar senhas resistentes a ataques de dicionário e força bruta.

- [ ] **MFA**: Adicionar suporte a autenticação multifator (2FA)
  - *Por quê?* Mesmo com senha comprometida, atacante precisa do segundo fator.
  - Reduz drasticamente risco de account takeover, especialmente para contas privilegiadas.

- [ ] **Bloqueio de Conta**: Bloquear conta após N tentativas de login falhas
  - *Por quê?* Permite tentativas ilimitadas facilita ataques de força bruta automatizados.
  - Bloqueio temporário ou permanente dificulta significativamente tentativas de invasão.

- [ ] **Session Management**: Configurar timeout de sessão e invalidação após logout
  - *Por quê?* Sessões eternas aumentam janela de oportunidade para session hijacking.
  - Timeouts forçam re-autenticação e limpeza adequada previne reutilização de tokens.

## 8) Software and Data Integrity Failures
- [ ] **CI/CD Pipeline**: Implementar pipeline com verificação de assinaturas e checksums
  - *Por quê?* Dependências podem ser alteradas maliciosamente em repositórios (supply chain attacks).
  - Verificação garante que código compilado é exatamente o que foi revisado e aprovado.

- [ ] **Digital Signatures**: Assinar builds de produção
  - *Por quê?* Sem assinatura, não há garantia de que o artefato deployado não foi adulterado.
  - Assinaturas permitem verificar autenticidade e integridade do software em produção.

- [ ] **SBOM**: Gerar Software Bill of Materials (SBOM) para rastreabilidade
  - *Por quê?* Quando CVE é divulgado, é crucial saber rapidamente se você está afetado.
  - SBOM fornece inventário completo de componentes para resposta rápida a incidentes.

- [ ] **Input Validation**: Validar integridade de arquivos enviados (se houver upload)
  - *Por quê?* Arquivos maliciosos podem conter malware, scripts ou exploits.
  - Validação de tipo, tamanho e conteúdo previne uploads perigosos que comprometem o servidor.

## 9) Security Logging and Monitoring Failures
- [ ] **Spring Boot Actuator**: Adicionar endpoints de health, metrics e auditevents
  - *Por quê?* Sem métricas, impossível detectar degradação de performance ou ataques DoS.
  - Actuator fornece visibilidade em tempo real do estado da aplicação.

- [ ] **Logback/SLF4J**: Configurar logging estruturado com níveis apropriados
  - *Por quê?* Logs desestruturados dificultam análise automatizada e correlação de eventos.
  - Formato estruturado (JSON) permite busca eficiente e integração com ferramentas de análise.

- [ ] **Audit Events**: Logar eventos críticos:
  - Tentativas de login (sucesso/falha)
  - Mudanças de permissões
  - Acesso negado (403)
  - Operações CRUD em recursos críticos
  - *Por quê?* Sem logs de auditoria, ataques passam despercebidos até causarem dano visível.
  - Logs permitem detectar padrões suspeitos, investigar incidentes e atender compliance.

- [ ] **ELK Stack**: Integrar com Elasticsearch, Logstash, Kibana para análise
  - *Por quê?* Logs espalhados em múltiplos servidores são impraticáveis de analisar manualmente.
  - Centralização permite correlação de eventos, dashboards e detecção de ameaças em tempo real.

- [ ] **Alertas**: Configurar alertas para comportamentos anômalos
  - *Por quê?* Monitoramento passivo é inútil se ninguém vê os problemas acontecendo.
  - Alertas proativos reduzem tempo de resposta a incidentes de horas para minutos.

## 10) Server-Side Request Forgery (SSRF)
- [ ] **URL Validation**: Se implementar features que aceitam URLs, criar whitelist de domínios permitidos
  - *Por quê?* SSRF permite atacante usar seu servidor para acessar recursos internos bloqueados externamente.
  - Whitelist impede que aplicação seja usada como proxy para atacar infraestrutura interna ou serviços cloud metadata.

- [ ] **Network Segmentation**: Configurar firewall para bloquear acesso a IPs internos (127.0.0.1, 169.254.x.x, 10.x.x.x)
  - *Por quê?* Aplicação pode acessar serviços internos (DBs, APIs admin) que não deveriam ser expostos.
  - Segmentação de rede limita blast radius mesmo se SSRF for explorado.

- [ ] **Timeout**: Configurar timeouts curtos em requisições HTTP externas
  - *Por quê?* Requisições para serviços lentos podem travar threads, causando DoS.
  - Timeouts curtos limitam impacto de ataques e protegem disponibilidade da aplicação.

- [ ] **Disable Redirects**: Desabilitar seguimento automático de redirects
  - *Por quê?* Atacante pode usar redirect para bypassar validação de URL inicial.
  - Desabilitar redirects previne bypass de whitelist e acesso a recursos não autorizados.

---

## 📊 Prioridades de Implementação

### 🔴 Alta Prioridade
1. Validação de Input (#3)
2. DTOs e separação de camadas (#4)
3. Política de senhas e JWT (#7)
4. Logging de eventos de segurança (#9)
5. Controle de acesso no UserController (#1)

### 🟡 Média Prioridade
6. Dependency Check automatizado (#6)
7. Spring Boot Actuator para monitoramento (#9)
8. CORS e Security Headers (#5)
9. Gestão de secrets (#2)
10. Perfis de ambiente (#5)

### 🟢 Baixa Prioridade
11. MFA (#7)
12. ELK Stack (#9)
13. SBOM e assinaturas digitais (#8)
14. Network segmentation para SSRF (#10)
