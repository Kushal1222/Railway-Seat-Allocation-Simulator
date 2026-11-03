/** Encapsulated passenger data. */
public class Passenger {
    private final String name;
    private final int age;
    private final String gender;      // Male / Female / Other
    private final String preference;  // Lower, Middle, Upper, Side Lower, Side Upper, No Preference

    public Passenger(String name, int age, String gender, String preference) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.preference = preference;
    }

    public String getName() { return name; }
    public int getAge() { return age; }
    public String getGender() { return gender; }
    public String getPreference() { return preference; }

    /** Senior citizens (60+) and women aged 45+ get priority for lower berths. */
    public boolean isPriority() {
        return age >= 60 || (gender.equals("Female") && age >= 45);
    }

    /** Senior citizen concession: 40% for men, 50% for women (60+). */
    public int getDiscountPercent() {
        if (age >= 60) return gender.equals("Female") ? 50 : 40;
        return 0;
    }
}
