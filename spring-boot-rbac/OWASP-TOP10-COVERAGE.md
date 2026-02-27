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


