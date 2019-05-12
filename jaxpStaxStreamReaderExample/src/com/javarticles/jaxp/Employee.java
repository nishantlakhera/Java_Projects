package com.javarticles.jaxp;

public class Employee {
    private String name;
    private Integer age;
    private Integer id;
    
    Employee(){}
    
    Employee(Integer id, String name, Integer age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Integer getAge() {
        return age;
    }
    public void setAge(Integer age) {
        this.age = age;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String toString() {
        return "Employee(id:"+ id + ", name:" + name + ", age:" + age + ")";
    }
}
