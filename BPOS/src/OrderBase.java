public class OrderBase implements Order {

    private double amount;

    OrderBase(double amount) {
        this.amount = amount;
    }

    @Override
    public double getTotalAmount() {
        return amount;
    }
}
