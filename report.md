# Báo cáo: So sánh thời gian CI trước và sau khi bật cache Gradle

## Cấu hình

Bước `actions/setup-java@v5` trong `.github/workflows/ci.yml` được bổ sung `cache: 'gradle'`.
`setup-java` lưu thư mục `~/.gradle/caches` và `~/.gradle/wrapper` với khóa (key) được tính từ
hash của các tệp `**/*.gradle*`, `**/gradle-wrapper.properties`. Ở lần chạy đầu, cache chưa tồn tại
nên được tạo và lưu ở bước *Post Thiết lập môi trường Java JDK 17*; từ lần chạy sau, cache được khôi phục.

## Kết quả đo

| Bước                                   | Lần 1 (chưa cache) | Lần 2 (đã cache) |
|----------------------------------------|--------------------|------------------|
| Thiết lập môi trường Java JDK 17       | 1 s                | 2 s              |
| Thực thi JUnit Test với Gradle         | 35 s               | 20 s             |
| Đóng gói tệp JAR với Gradle            | 1 s                | 1 s              |
| Post Thiết lập môi trường Java JDK 17  | 4 s (lưu cache)    | 0 s              |
| **Tổng thời gian Job Run Unit Tests**  | **49 s**           | **29 s**         |

- Lần 1: https://github.com/Gaabuto/ss11-ex3/actions/runs/38252244690
- Lần 2: https://github.com/Gaabuto/ss11-ex3/actions/runs/38252359960

## Log xác nhận cache

Lần 1 – bước setup-java và bước Post:

```
gradle cache is not found
...
Cache saved with the key: setup-java-Linux-x64-gradle-2fd296b47f7bfa67e4e525131bf596038161adce34c4e212af18d8824fe1134e
gradle-wrapper cache saved with the key: setup-java-Linux-x64-gradle-wrapper-cb10f63b6e80d04c47ba63d0097b1be1d92513baebbf33fb9b91e65214965f4c
```

Lần 2 – bước setup-java:

```
Cache hit for: setup-java-Linux-x64-gradle-2fd296b47f7bfa67e4e525131bf596038161adce34c4e212af18d8824fe1134e
Cache Size: ~47 MB (49796786 B)
Cache restored successfully
Cache restored from key: setup-java-Linux-x64-gradle-2fd296b47f7bfa67e4e525131bf596038161adce34c4e212af18d8824fe1134e
Cache hit for: setup-java-Linux-x64-gradle-wrapper-cb10f63b6e80d04c47ba63d0097b1be1d92513baebbf33fb9b91e65214965f4c
Cache Size: ~144 MB (150683858 B)
Cache restored successfully
gradle-wrapper cache restored from key: setup-java-Linux-x64-gradle-wrapper-cb10f63b6e80d04c47ba63d0097b1be1d92513baebbf33fb9b91e65214965f4c
```

## Nhận xét

- Lần 1: Gradle phải tải Gradle distribution (9.7.1) và toàn bộ thư viện (Spring Boot, JUnit, ...)
  từ Internet, sau đó mất thêm 4 s ở bước Post để lưu cache.
- Lần 2: khôi phục ~191 MB cache (47 MB thư viện + 144 MB Gradle wrapper) chỉ tốn ~1 s thêm ở bước
  setup-java, nhưng bước `./gradlew test` giảm từ 35 s xuống 20 s (nhanh hơn ~43%).
- Tổng thời gian Job giảm từ 49 s xuống 29 s, tức giảm khoảng **41%**.
