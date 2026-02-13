# 🔐 Spring Security com Anotações (Method Security)

Guia completo e prático para implementar **autenticação e autorização declarativa** usando **Spring Boot + Spring Security + Anotações**.

Foco principal:
- @PreAuthorize
- @PostAuthorize
- @Secured
- @RolesAllowed
- Integração com JWT
- Boas práticas para APIs REST

---

# 📌 Objetivo

Este guia ensina como proteger endpoints e métodos de forma **limpa, declarativa e profissional**, evitando `if/else` manuais e centralizando regras de segurança.

Ideal para:
- APIs REST
- Microservices
- Projetos com JWT
- Sistemas corporativos

---

# 🧠 Conceitos Fundamentais

## Autenticação vs Autorização

| Conceito | Pergunta | Exemplo |
|----------|-----------|-----------|
| Autenticação | Quem é você? | login/senha/JWT |
| Autorização | O que pode fazer? | ADMIN pode deletar |

---

# ⚙️ Como o Spring Security funciona

Fluxo interno:

Request  
→ Filtros (JWT / Session)  
→ Authentication criado  
→ SecurityContext  
→ Anotações avaliadas  
→ Método executa ou 403  

Erros:
- 401 → não autenticado
- 403 → sem permissão

---

# 🚀 Dependências

## Maven

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

---

# 🔧 Habilitando segurança por anotações

Spring Boot 3+

```java
@Configuration
@EnableMethodSecurity
public class SecurityConfig {
}
```

Sem isso as anotações NÃO funcionam.

---

# 🎯 Principais Anotações

## @PreAuthorize (RECOMENDADA)

Executa antes do método.

```java
@PreAuthorize("hasRole('ADMIN')")
```

Exemplos:

```java
@PreAuthorize("hasAnyRole('ADMIN','USER')")
@PreAuthorize("hasAuthority('WRITE_PRIVILEGE')")
@PreAuthorize("#id == authentication.principal.id")
@PreAuthorize("hasRole('ADMIN') or #userId == authentication.name")
```

---

## @PostAuthorize

```java
@PostAuthorize("returnObject.owner == authentication.name")
```

---

## @Secured

```java
@Secured("ROLE_ADMIN")
```

---

## @RolesAllowed

```java
@RolesAllowed({"ADMIN","MANAGER"})
```

---

# 🧪 Exemplo Controller

```java
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Usuario criar(@RequestBody Usuario u) {
        return service.salvar(u);
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping
    public List<Usuario> listar() {
        return service.listar();
    }

    @PreAuthorize("#id == authentication.principal.id or hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}
```

---

# 🔐 Integração com JWT

```java
List<GrantedAuthority> authorities =
    roles.stream()
         .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
         .toList();

Authentication auth =
    new UsernamePasswordAuthenticationToken(user, null, authorities);

SecurityContextHolder.getContext().setAuthentication(auth);
```

---

# 🧰 Boas Práticas

Faça:
- Use @PreAuthorize
- Proteja Services
- Use JWT stateless

Evite:
- if/else manual
- regras no frontend

---

# ✅ Conclusão

Para APIs REST modernas com Spring Boot + JWT:

👉 Use @PreAuthorize.
