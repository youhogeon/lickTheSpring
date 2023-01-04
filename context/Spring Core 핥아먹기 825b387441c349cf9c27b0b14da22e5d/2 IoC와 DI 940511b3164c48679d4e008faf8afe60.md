# 2. IoC와 DI

# 개요

Spring Framework를 처음 공부할 때 가장 어렵게 느낀 부분이 IoC와 DI에 대한 개념

- DI(Dependency Injection; 의존성주입)는 클래스들 간의 연관관계를 클래스 내 코드로 작성하게 되면 강한 결합이 생기기 때문에, 이를 방지하고자 외부에서 주입하겠다는 의미
- IoC(Inversion of Control; 제어의 역전) 는 DI를 개발자가 하는 것이 아니라 프레임워크가 대신 해준다는 의미

# SOLID 원칙

객체 지향 프로그래밍 설계의 다섯 가지 기본 원칙

- S (Single responsibility principle; 단일 책임 원칙)
- O (Open-closed principle; 개방 폐쇄 원칙)
- L (Liskov substitution principle; 리스코프 치환 원칙)
- I (Interface segregation principle; 인터페이스 분리 원칙)
- D (Dependency inversion principle; 의존관계 역전 원칙)

## S(SRP) / 단일 책임 원칙

> “한 클래스는 하나의 책임만 가져야 한다”
> 

```java
//Bad Case
public class AccountRepository {

	public void update(Account account) { ... }
	public void validate(Account account) { ... }

}
```

위 코드는 AccountRepository가 값 저장, 값 검증 이라는 두 가지 책임을 가지고 있으므로 SRP 원칙에 위배된다.

AccountRepository는 값 저장(혹은 생성 삭제)에만 책임을 지고, 값 검증과 같은 책임은 Account 클래스가 지도록 해야 한다.

## O(OCP) / 개방 폐쇄 원칙

> “소프트웨어 요소는 확장에는 열려 있으나 변경에는 닫혀 있어야 한다”
> 

(확장은 언제든 가능해야 하고, 주변이 변경될 때 영향 받지 않아야 한다)

```java
//Bad Case
public class AccountRepository {

	private final DataSource dataSource = new OracleDataSource();
	
	public void update(Account account) { ... }

}
```

위 코드는 새로운 DBMS를 지원해야하는 일이 생기면(확장이 일어나면) 새로운 DataSource Class를 만드는 것으로 확장이 가능하다.

그러나, 기존 코드를 변경(new OracleDataSource()를 new MySQLDataSource()로 변경하는 등) 해야 하는 문제가 있다.

```java
//Good Case
public class AccountRepository {

	private final DataSource dataSource;

	public AccountRepository(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void update(Account account) { ... }

}
```

이렇게 외부에서 DataSource 타입의 객체를 생성자를 통해 주입받으면, DBMS가 변경되더라도 AccountRepository의 코드를 바꿀 필요가 없어진다.

## L(LSP) / 리스코프 치환 원칙

> “프로그램의 객체는 프로그램의 정확성을 깨뜨리지 않으면서 하위 타입의 인스턴스로 바꿀 수 있어야 한다.”
> 

```java
class A { ... }

class B extends A { ... }
```

A에서 가능했던 동작이 B에서도 가능해야 한다.

부모 클래스의 역할을 상속받아서 구현하되, 부모 클래스의 설계에 어긋나면 안된다.

```java
//Bad Case
class AccountEntity { 

	String id;
	String name;

}
```

Java에서 모든 클래스는 Object를 암묵적으로 상속하기 때문에, Object의 equals 메서드와 hashCode 메서드 역시 모든 클래스에서 올바르게 동작해야 한다.

그러나 위의 AccountEntity는 동일한 id와 name을 가진 두 객체가 not equals 하다고 판단하기 때문에, LSP에 위배된다.

따라서 equals와 hashCode 메서드를 올바르게 재정의해야 한다.

## I(ISP) / 인터페이스 분리 원칙

> “특정 클라이언트를 위한 인터페이스 여러 개가 범용 인터페이스 하나보다 낫다.”
> 

많은 구현 요구사항이 있는 하나의 범용 인터페이스보다, 잘개 쪼갠 여러개의 인터페이스가 낫습니다. (인터페이스를 구현할 때 불필요한 기능 구현을 방지할 수 있기 때문)

## D(DIP) / 의존관계 역전 원칙

> “추상화에 의존해야지, 구체화에 의존하면 안된다."
> 

```java
//Bad Case
public class AccountRepository {

	private final OracleDataSource dataSource;

	public AccountRepository(OracleDataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void update(Account account) { ... }

}
```

위 코드는 OracleDataSource 라는 구체 클래스에 의존하고 있습니다.

만약 DBMS를 변경해야 한다면, AccountRepository 코드까지 변경해야 할 것입니다.

따라서 DataSource 인터페이스에 의존하도록 변경해야 합니다.

```java
//Good Case
public class AccountRepository {

	private final DataSource dataSource;

	public AccountRepository(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void update(Account account) { ... }

}
```

# DI (IoC) 프레임워크가 필요한 이유

SOLID원칙을 준수하기 위해서는 두 클래스가 강하게 결합되지 않도록 해야 합니다.

따라서 생성자 등을 통해 외부로부터 구체 클래스 객체를 주입받아야 합니다.

DI 프레임워크를 사용하게되면, 이러한 객체 주입을 편하게 처리할 수 있습니다.

```java
public class AccountRepository {

	@Autowired
	private DataSource dataSource;

	public void update(Account account) { ... }

}
```

DI 프레임워크 중 하나인 스프링을 사용하게 되면, 위와 같이 @Autowired 어노테이션을 필드에 붙이는 것 만으로도 외부로부터 DataSource 구현 객체를 주입받을 수 있습니다.

구현 객체를 생성(new xxxxDataSource())하고, 해당 필드에 값을 넣는 것은 스프링 프레임워크에서 알아서 해주기 때문에, 스프링 프레임워크를 IoC (제어의 역전) 프레임워크라고도 부릅니다.