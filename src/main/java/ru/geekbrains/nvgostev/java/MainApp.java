package ru.geekbrains.nvgostev.java;

public class MainApp {
    public static void main(String[] args) {
        try {
            Db.connect();

            Db.add("Bob1 28 Bob1@email.com");
            Db.add("Bob2 33 Bob2@email.com");
            Db.add("Bob3 41 Bob3@email.com");
            Db.add("Bob4 60 Bob4@email.com");
            Db.add("Bob5 19 Bob5@email.com");
            System.out.printf("Users:\n%s\n", Db.getAllUsers());

            int min = 20;
            int max = 35;
            System.out.printf("Users by age:\n%s\n", Db.getUsersByAge(min, max));

            Db.delUserByName("Bob4");
            System.out.printf("Was deleted: %s\n%s\n", "Bob4", Db.getAllUsers());

         } finally {
            Db.disconnect();
        }
    }

}
