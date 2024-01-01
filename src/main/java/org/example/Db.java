package org.example;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.Query;

import java.sql.*;
import java.util.List;

public class Db {
    private static final String URL = "jdbc:mysql://localhost:3306";
    private static final String USER = "root";
    private static final String PASSWORD = "admin";


    public static void con() {
        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD)) {
            Statement statement = con.createStatement();
            statement.execute("DROP SCHEMA `test`");
            statement.execute("CREATE SCHEMA `test`");
            statement.execute("CREATE TABLE `test`.`table` (`id` INT NOT NULL, `firstname` VARCHAR(45) NULL, `lastname` VARCHAR(45) NULL, PRIMARY KEY(`id`));");
            statement.execute("INSERT INTO  `test`.`table` (`id`,`firstname`,`lastname`)\n" +
            "VALUES (1, 'Иванов','Иван');");
            statement.execute("INSERT INTO  `test`.`table` (`id`,`firstname`,`lastname`)\n" +
                    "VALUES (2, 'Петров','Петр');");

            //выборка
            ResultSet set = statement.executeQuery("SELECT * FROM test.table;");
            while (set.next()){
                System.out.println(set.getString(3) + " " + set.getString(2) + " " + set.getInt(1));


            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public static void workBilder() {
        // эта переменная класса, который содержит в себе
        // механизмы для связи с сервером баз данных и
        // менеджером передачи запросов, именно этому классу
        // нужен конфиг файл .xml
        final StandardServiceRegistry registry =
                new StandardServiceRegistryBuilder()
                        .configure()
                        .build();
        // далее SessionFactory - это неизменяемый потокобезопасный обьект
        // с компилированным мапингом для одной базы данных
        // его необходимо компилировать всего один раз
        // этот обьект используется для получения обьектов
        // session, которые используется для операций с базами данных
        SessionFactory sessionFactory = new MetadataSources(registry)
                .buildMetadata().buildSessionFactory();
        Session session = sessionFactory.openSession();
        // создаем обьект класса мапинг
        Magic magic = new Magic("Волшебная стрела",10,0,0);
        //
        session.beginTransaction();            //начинаем транзакцию
        session.save(magic);                   // сохраняем наш обьект в БД
        session.getTransaction().commit();     // выводим и выполняем и завершаем транзакцию
        session.close();                       //закрываем сессию
    }

    public static void sessionConnector(){
        Connector connector = new Connector();
        Session session = connector.getSession();
        Magic magic = new Magic("Волшебная стрела",10,0,0);
        session.beginTransaction();
        session.save(magic);
        magic = new Magic("Молния",25,0,0);
        session.save(magic);
        magic = new Magic("Каменная кожа",0,0,0);
        session.save(magic);
        magic = new Magic("Жажда крови",0,6,0);
        session.save(magic);
        magic = new Magic("Жажда крови",0,6,0);
        session.save(magic);
        magic = new Magic("Проклятие",0,-3,0);
        session.save(magic);
        magic = new Magic("Лечение",-30,0,0);
        session.getTransaction().commit();
        session.save(magic);
        session.close();

    }

    //получение данных из БД
    public static void gettingDataFromTable(){
        Connector connector = new Connector();
        try (Session session = connector.getSession()){
            List<Magic> books = session.createQuery("FROM Magic ", Magic.class).getResultList();
            books.forEach(b -> {
                System.out.println(b);
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //изменение обьектов
    public static void changingObjects(){
        Connector connector = new Connector();
        try (Session session = connector.getSession()){
           //строка хранит запрос на языке hql
            String hql = "from Magic where id = :id";
            Query<Magic> query = session.createQuery(hql, Magic.class);
            query.setParameter("id",4);
            Magic magic = query.getSingleResult();
            System.out.println(magic);
            magic.setAttBonus(100);
            magic.setName("Ярость");
            session.beginTransaction();
            session.update(magic);
            session.getTransaction().commit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // удаление обьектов (в лямбде условие удаления)
    public static void deleteOb(){
        Connector connector = new Connector();
        try (Session session = connector.getSession()){
            Transaction t = session.beginTransaction();
            List<Magic> magics = session.createQuery("FROM Magic ", Magic.class).getResultList();
            magics.forEach(m-> {
                session.delete(m);
            });
            t.commit();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
