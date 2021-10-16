public class AllowGiftedOrder extends OrderDecorator {
    AllowGiftedOrder(Order order) {
        super(order);
    }

    @Override
    public double getTotalAmount() {
        return order.getTotalAmount() + 100;
    }
}
