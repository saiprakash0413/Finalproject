import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws InterruptedException, CloneNotSupportedException {
        HashMap<String, Integer> counterMap = new HashMap<>();
        counterMap.put("customer", 0);
        counterMap.put("author", 0);
        counterMap.put("publisher", 0);
        counterMap.put("system", 0);

        ObjectFactory factory = new PersonFactory();

        HashMap<String, HashMap<Integer, Person>> map = new HashMap<>();
        HashMap<Integer, Person> customers = new HashMap<>();
        HashMap<Integer, Person> publishers = new HashMap<>();
        HashMap<Integer, Person> authors = new HashMap<>();
        HashMap<Integer, BPOSytem> systems = new HashMap<>();
        map.put("customer", customers);
        map.put("publisher", publishers);
        map.put("author", authors);

        System.out.println("=========== Console =============");
        System.out.println("");
        System.out.println("Possible Queries: ");
        System.out.println("1. customer:create");
        System.out.println("2. author:create");
        System.out.println("3. publisher:create");
        System.out.println("3. system:create");
        System.out.println("4. publisher:follow:<publisher_id>:<author_id>");
        System.out.println("5. system:follow:<system_id>:<publisher_id>");
        System.out.println("6. publisher:unfollow:<publisher_id>:<author_id>");
        System.out.println("7. system:unfollow:<system_id>:<publisher_id>");
        System.out.println("8. author:write:<author_id>");
        System.out.println("9. publisher:publish:<publisher_id>");
        System.out.println("10. system:available:<system_id>");
        System.out.println("11. customer:display:system:<customer_id>:<system:id>");
        System.out.println("12. customer:search:system:<customer_id>:<system:id>");
        System.out.println("13. customer:display:cart:<customer_id>:<system:id>");
        System.out.println("14. customer:remove:cart:<customer_id>:<system:id>");
        System.out.println("15. customer:checkout:cart:<customer_id>:<system:id>");

        System.out.println("");
        System.out.println("=================================");
        System.out.println("");
        System.out.println("=================================");

        while(true) {
            System.out.print("Enter Your Query: ");
            Scanner scanner = new Scanner(System.in);
            String query = scanner.next();
            System.out.println("");
            String[] queryArgs = query.split(":");
            if (queryArgs[1].equals("create")) {
                if(queryArgs[0].equals("system")) {
                    System.out.println("Your system ID is " + counterMap.get(queryArgs[0]));
                    BPOSytem system = new BPOSytem(counterMap.get(queryArgs[0]));
                    systems.put(counterMap.get(queryArgs[0]), system);
                    int count = counterMap.get(queryArgs[0]);
                    counterMap.put(queryArgs[0], count + 1);
                    System.out.println("[Info] System Added");
                } else {
                    System.out.println("Your customer ID is " + counterMap.get(queryArgs[0]));
                    System.out.print("Enter Your Name : ");
                    String name = scanner.next();
                    System.out.print("Enter Your Address: ");
                    String address = scanner.next();
                    Person person = (Person) factory.getObject(queryArgs[0], "" + counterMap.get(queryArgs[0]), name, address);
                    HashMap<Integer, Person> users = map.get(queryArgs[0]);
                    users.put(counterMap.get(queryArgs[0]), person);
                    map.put(queryArgs[0], users);

                    int count = counterMap.get(queryArgs[0]);
                    counterMap.put(queryArgs[0], count + 1);
                    System.out.println("[Info] " + queryArgs[0] + " Added");
                }
            } else if(queryArgs[1].equals("follow") || queryArgs[1].equals("unfollow")) {
                int firstId = Integer.parseInt(queryArgs[2]);
                int secondID = Integer.parseInt(queryArgs[3]);
                if (queryArgs[0].equals("publisher")) {
                    Publisher publisher = (Publisher) map.get("publisher").get(firstId);
                    Author author = (Author) map.get("author").get(secondID);
                    if(queryArgs[1].equals("follow")) author.publisherFollow(publisher);
                    else author.publisherUnFollow(publisher);
                } else if(queryArgs[0].equals("system")) {
                    Publisher publisher = (Publisher) map.get("publisher").get(secondID);
                    BPOSytem system = systems.get(firstId);
                    if(queryArgs[1].equals("follow")) publisher.systemFollow(system);
                    else publisher.systemUnFollow(system);
                }
            } else if(queryArgs[1].equals("write") || queryArgs[0].equals("author")) {
                int id = Integer.parseInt(queryArgs[2]);
                Author author = (Author) map.get("author").get(id);
                System.out.print("Enter the Title: ");
                String title = scanner.next();
                Book book = new Book();
                book.setTitle(title);
                author.writes(book);
            } else if(queryArgs[1].equals("publish") || queryArgs[0].equals("publisher")) {
                int id = Integer.parseInt(queryArgs[2]);
                ((Publisher) map.get("publisher").get(id)).publishAvailableBooks();
            } else if(queryArgs[1].equals("available") || queryArgs[0].equals("system")) {
                int id = Integer.parseInt(queryArgs[2]);
                systems.get(id).buyAvailableBooks();
            } else if(queryArgs[0].equals("customer")) {
                int customerId = Integer.parseInt(queryArgs[3]);
                int systemID = Integer.parseInt(queryArgs[4]);
                Customer customer = (Customer) map.get("customer").get(customerId);
                BPOSytem system = systems.get(systemID);
                if(queryArgs[2].equals("system")) {
                    if(queryArgs[1].equals("display")) system.displayAllBooks();
                    else if(queryArgs[1].equals("search")) {
                        System.out.print("Enter the title: ");
                        String title = scanner.next();
                        Book book = system.getBook(title);
                        System.out.print("Do you want to add this book into your cart (yes|no): ");
                        String choice = scanner.next();
                        if(choice.toLowerCase().equals("yes")) customer.addBook(book);
                    }
                } else if (queryArgs[2].equals("cart")) {
                    if(queryArgs[1].equals("display")) customer.displayCartItems();
                    else if(queryArgs[1].equals("remove")) {
                        System.out.print("Enter the title which you want to remove from the cart: ");
                        String title = scanner.next();
                        Book book = customer.getCartBook(title);
                        customer.removeBook(book);
                    } else if(queryArgs[1].equals("checkout")) {
                        customer.placeOrder();
                        List<Book> books = customer.getCartItems();
                        System.out.print("Do you want to add delivery option (yes|no)");
                        String choice = scanner.next();
                        if(choice.toLowerCase().equals("yes")) customer.addDelivery();

                        System.out.print("Do you want to pack these books as gift (yes|no)");
                        choice = scanner.next();
                        if(choice.toLowerCase().equals("yes")) customer.addGift();

                        System.out.println();
                        double amount = customer.getCartAmount();
                        System.out.println("[Info] Total Amount: " + amount);
                        System.out.println("[Info] Payment in process ...........");
                        System.out.println();
                        Thread.sleep(5000);
                        customer.checkOut(amount);
                        for(Book book: books) system.removeBook(book);
                    }
                }
            } else {
                System.out.println("[Error] Not Supported Query");
            }
            System.out.println();
        }
    }
}
