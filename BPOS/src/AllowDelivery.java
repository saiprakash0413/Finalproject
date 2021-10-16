public class AllowDelivery extends OrderDecorator {

    AllowDelivery(Order order) {
        super(order);
    }

    @Override
    public double getTotalAmount() {
        return order.getTotalAmount() + 50;
    }
}
