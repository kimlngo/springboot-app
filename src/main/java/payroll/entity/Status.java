package payroll.entity;

public enum Status {
    IN_PROGRESS("in-progress"), COMPLETED("complete"), CANCELLED("cancel");

    public final String operation;

    Status(String operation) {
        this.operation = operation;
    }
}