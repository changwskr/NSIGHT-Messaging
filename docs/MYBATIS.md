# MyBatis 환경 구성

이 프로젝트는 **JPA가 아닌 MyBatis** 기반으로 DB에 접근합니다.

## 1. 구성 요소

| 구분 | 경로 / 설정 |
|------|-------------|
| 전역 설정 | `src/main/resources/mybatis/mybatis-config.xml` |
| Spring 연동 | `config/MybatisConfig.java` (`@MapperScan`) |
| Mapper XML | `src/main/resources/mapper/**/*.xml` |
| Mapper Interface | `**/mapper/*Mapper.java` |
| DAO | `**/dao/*Dao.java` (Mapper 위임) |
| 연결 풀 | HikariCP (`spring.datasource.hikari`) |

## 2. application.yml

```yaml
mybatis:
  config-location: classpath:mybatis/mybatis-config.xml
  mapper-locations: classpath:mapper/**/*.xml
  type-aliases-package: com.nh.nsight.messaging.message.thing,com.nh.nsight.messaging.file.thing
```

## 3. 계층 흐름

```text
Controller / Facade / Service
    → Dao (@Repository)
    → Mapper Interface (@Mapper)
    → Mapper XML (SQL)
    → H2 / Oracle
```

## 4. Mapper 목록

| Mapper | XML | 테이블 |
|--------|-----|--------|
| MessageMapper | mapper/message/MessageMapper.xml | TB_MSG_MESSAGE |
| FileMapper | mapper/file/FileMapper.xml | TB_MSG_FILE |

## 5. 페이징 (Oracle / H2 Oracle 모드)

```sql
ORDER BY ...
OFFSET #{offset} ROWS FETCH NEXT #{safePageSize} ROWS ONLY
```

## 6. 의존성 (pom.xml)

```xml
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
</dependency>
```
