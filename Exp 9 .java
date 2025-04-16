CREATE DATABASE studentdb;

USE studentdb;

-- Table will be auto-created by Hibernate, no need to define manually

HibernateCRUD/
├── src/
│   ├── Student.java
│   ├── HibernateUtil.java
│   ├── StudentDao.java
│   └── MainApp.java
├── hibernate.cfg.xml
├── lib/ (Put all Hibernate & MySQL connector jars here)

<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE hibernate-configuration PUBLIC
        "-//Hibernate/Hibernate Configuration DTD 3.0//EN"
        "http://www.hibernate.org/dtd/hibernate-configuration-3.0.dtd">

<hibernate-configuration>
    <session-factory>
        <!-- JDBC Settings -->
        <property name="hibernate.connection.driver_class">com.mysql.cj.jdbc.Driver</property>
        <property name="hibernate.connection.url">jdbc:mysql://localhost:3306/studentdb</property>
        <property name="hibernate.connection.username">root</property>
        <property name="hibernate.connection.password">yourpassword</property>

        <!-- Hibernate Settings -->
        <property name="hibernate.dialect">org.hibernate.dialect.MySQL8Dialect</property>
        <property name="hibernate.hbm2ddl.auto">update</property>
        <property name="show_sql">true</property>

        <!-- Mapping class -->
        <mapping class="Student"/>
    </session-factory>
</hibernate-configuration>

import javax.persistence.*;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private int age;

    public Student() {}

    public Student(String name, int age) {
        this.name = name;
        this.age = age;
    }

    // Getters and Setters
    public int getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    @Override
    public String toString() {
        return "Student{id=" + id + ", name='" + name + "', age=" + age + "}";
    }
}

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static final SessionFactory sessionFactory;

    static {
        try {
            sessionFactory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError("Initial SessionFactory creation failed " + ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}

import org.hibernate.Session;
import org.hibernate.Transaction;

public class StudentDao {

    public void saveStudent(Student student) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(student);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public Student getStudent(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Student.class, id);
        }
    }

    public void updateStudent(Student student) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(student);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public void deleteStudent(int id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Student student = session.get(Student.class, id);
            if (student != null) {
                session.delete(student);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }
}

public class MainApp {
    public static void main(String[] args) {
        StudentDao dao = new StudentDao();

        // Create
        Student s1 = new Student("Alice", 20);
        dao.saveStudent(s1);
        System.out.println("Student saved: " + s1);

        // Read
        Student fetched = dao.getStudent(s1.getId());
        System.out.println("Student fetched: " + fetched);

        // Update
        fetched.setAge(22);
        dao.updateStudent(fetched);
        System.out.println("Student updated: " + fetched);

        // Delete
        dao.deleteStudent(fetched.getId());
        System.out.println("Student deleted.");
    }
}
