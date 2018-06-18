# createPlusBuild
idea plugin for java code generate.
To use it, search createPlusBuild in idea plugin repository.
As is the function, provided
```java
public class A {
    private Integer id;

    public Integer getId() {
        return id;
    }

    public A setId(Integer id) {
        this.id = id;
        return this;
    }
}

public class B {
    private Integer id;

    public Integer getId() {
        return id;
    }
```
It can generate
```java
public A create(B b) {
    A a = new A();
    a.setId(b.getId());
    return a;
}
```