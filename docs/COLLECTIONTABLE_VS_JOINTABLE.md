# @CollectionTable vs @JoinTable - Farklar ve Kullanım

## Temel Fark

### @CollectionTable (Şu Anki Kullanım)
- **Kullanım:** `@ElementCollection` ile birlikte kullanılır
- **Amaç:** Value types (String, Enum, Embeddable sınıflar) için collection'ları saklamak
- **Örnek:** Enum'lar, String listeleri, embeddable objeler

### @JoinTable
- **Kullanım:** `@ManyToMany` veya `@OneToMany` ile birlikte kullanılır
- **Amaç:** Entity-to-entity ilişkileri için join table oluşturmak
- **Örnek:** User-Role ilişkisi (Role bir Entity ise)

## Bizim Durumumuz

### Şu Anki Kod (Doğru Kullanım)
```java
@ElementCollection(fetch = FetchType.EAGER)
@Enumerated(EnumType.STRING)
@CollectionTable(name = "t_user_roles", joinColumns = @JoinColumn(name = "user_id"))
@Column(name = "role")
private Set<Role> roles = new HashSet<>();

public enum Role {
    ROLE_ADMIN, ROLE_USER
}
```

**Neden @CollectionTable?**
- `Role` bir **Enum** (value type)
- `@ElementCollection` kullanıldığı için `@CollectionTable` gerekli
- Enum değerleri direkt olarak String olarak saklanır
- Basit ve performanslı

### Alternatif: @JoinTable (Role Entity Olsa)

Eğer Role bir Entity olsaydı:

```java
// Role Entity
@Entity
@Table(name = "t_roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String name; // ROLE_ADMIN, ROLE_USER
    
    // getters, setters
}

// User Entity
@Entity
public class User {
    // ...
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "t_user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
}
```

**Neden @JoinTable?**
- `Role` bir **Entity** (bağımsız tablo)
- `@ManyToMany` kullanıldığı için `@JoinTable` gerekli
- Foreign key ilişkisi var
- Daha fazla esneklik (Role tablosunda ek bilgiler saklanabilir)

## Karşılaştırma Tablosu

| Özellik | @CollectionTable | @JoinTable |
|---------|------------------|------------|
| Kullanım | @ElementCollection | @ManyToMany, @OneToMany |
| Veri Tipi | Value types (Enum, String, Embeddable) | Entity types |
| Tablo Yapısı | Basit (user_id, value) | Join table (user_id, role_id) |
| Performans | Daha hızlı | Biraz daha yavaş (join gerekir) |
| Esneklik | Sınırlı | Daha esnek (Role entity'de ek bilgiler) |
| Karmaşıklık | Basit | Daha karmaşık |

## Veritabanı Yapısı

### @CollectionTable ile (Şu Anki)
```sql
CREATE TABLE t_user_roles (
    user_id BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL,  -- Enum değeri direkt String olarak
    PRIMARY KEY (user_id, role),
    FOREIGN KEY (user_id) REFERENCES t_users(id)
);
```

### @JoinTable ile (Role Entity Olsa)
```sql
-- Role tablosu
CREATE TABLE t_roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- Join table
CREATE TABLE t_user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES t_users(id),
    FOREIGN KEY (role_id) REFERENCES t_roles(id)
);
```

## Hangisini Kullanmalıyız?

### @CollectionTable Kullan (Şu Anki - Önerilen) ✅
- Role sadece bir enum (basit değer)
- Rol adları değişmeyecek (ROLE_ADMIN, ROLE_USER)
- Performans önemli
- Basit yapı yeterli

### @JoinTable Kullan
- Role'ler dinamik olarak eklenip çıkarılacak
- Role'lerde ek bilgiler saklanacak (açıklama, yetkiler, vs.)
- Role'ler başka entity'lerle de ilişkili olacak
- Daha karmaşık bir role yönetim sistemi gerekiyor

## Sonuç

Bizim durumumuzda **@CollectionTable doğru seçim** çünkü:
1. Role bir enum (value type)
2. Basit ve performanslı
3. Roller sabit (ROLE_ADMIN, ROLE_USER)
4. Ek bilgilere ihtiyaç yok

Eğer gelecekte Role'leri dinamik yönetmek ve ek bilgiler eklemek gerekirse, o zaman Role'ü bir Entity'ye dönüştürüp @JoinTable kullanabiliriz.

