package HW1HashMap;

public class Main {
    public static void main(String[] args) {

        MyHashMap<String, Integer> map = new MyHashMap<>();

        //метод PUT
        map.put("A", 10);
        map.put("B", 20);
        map.put("C", 30);
        System.out.println("Добавленные значения:");
        System.out.println("A = " + map.get("A"));
        System.out.println("B = " + map.get("B"));
        System.out.println("C = " + map.get("C"));

        //метод GET
        System.out.println("\nПолучение значения по ключу 'B':");
        Integer value = map.get("B");
        System.out.println("B = " + value);

        //метод REMOVE
        System.out.println("\nУдаление ключа 'B'");
        Integer removed = map.remove("B");
        System.out.println("Удалённое значение: " + removed);
        System.out.println("\nПроверка после удаления:");
        System.out.println("B = " + map.get("B"));
    }
}
