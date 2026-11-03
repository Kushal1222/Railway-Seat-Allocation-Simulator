/** Custom checked exception used for validation errors (bad name, age, date...). */
public class InvalidInputException extends Exception {
    public InvalidInputException(String message) {
        super(message);
    }
}
