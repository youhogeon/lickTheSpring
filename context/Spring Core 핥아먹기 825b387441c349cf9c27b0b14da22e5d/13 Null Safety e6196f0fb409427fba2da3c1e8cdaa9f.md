# 13. Null Safety

# Null Safety

Java는 기본적으로 null-safety를 표현할 수 없음. (안전을 위해 Optional을 사용 권장)

Spring의 null-safety 어노테이션을 사용해 해당 매개변수, 반환 값, 필드가 null이 될 수 없음을 명시할 수 있음. (강제할 수는 없음.)

- @Nullable - 특정 매개변수, 반환 값, 필드가 null이 될 수 있음을 명시
- @NonNull - 특정 매개변수, 반환 값, 필드가 null이 될 수 없음을 명시
- @NonNullApi - null이 아닌 것을 매개변수, 반환 값의 기본 체계로 선언(패키지 수준)
- @NonNullFields - null이 아닌 것을 필드의 기본 체계로 선언(패키지 수준)

```java
public void print(@NonNull String message) {
    System.out.println(message);
}

public void run() {
    print(null);
}
```

강제 사항이 아니기 때문에 컴파일/실행이 잘 되지만, (지원하는) IDE에서 경고를 띄워준다.

![Untitled](13%20Null%20Safety%20e6196f0fb409427fba2da3c1e8cdaa9f/Untitled.png)