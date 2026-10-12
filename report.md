# Báo cáo: So sánh thời gian CI trước và sau khi bật cache Gradle

## Cấu hình

Bước `actions/setup-java@v5` trong `.github/workflows/ci.yml` được bổ sung `cache: 'gradle'`.
`setup-java` sẽ lưu thư mục `~/.gradle/caches` và `~/.gradle/wrapper` với khóa (key) được tính từ
hash của các tệp `**/*.gradle*`, `**/gradle-wrapper.properties`. Ở lần chạy đầu, cache chưa tồn tại
nên được tạo và lưu ở bước *Post Thiết lập môi trường Java JDK 17*; từ lần chạy sau, cache được khôi phục.

## Kết quả đo

| Bước                                  | Lần 1 (chưa cache) | Lần 2 (đã cache) |
|---------------------------------------|--------------------|------------------|
| Thiết lập môi trường Java JDK 17      | ... s              | ... s            |
| Thực thi JUnit Test với Gradle        | ... s              | ... s            |
| Đóng gói tệp JAR với Gradle           | ... s              | ... s            |
| **Tổng thời gian Job Run Unit Tests** | **... s**          | **... s**        |

Link run lần 1: ...
Link run lần 2: ...

## Log xác nhận cache

Lần 1 (bước setup-java):

```
Gradle cache is not found
```

Lần 2 (bước setup-java):

```
Cache restored successfully from key: gradle-...
```

## Nhận xét

- Lần 1: Gradle phải tải Gradle distribution và toàn bộ thư viện (Spring Boot, JUnit, ...) từ Internet.
- Lần 2: thư viện được khôi phục từ cache nên bước `./gradlew test` nhanh hơn khoảng ... s,
  tổng thời gian Job giảm khoảng ...%.
