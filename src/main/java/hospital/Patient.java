
package hospital;

public class Patient {

    private int patientId;
    private String name;
    private int age;
    private String gender;
    private String contact;

    public Patient(int patientId, String name, int age,
                   String gender, String contact) {
        this.patientId = patientId;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.contact = contact;
    }

    public int getPatientId() {
        return patientId;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public String getContact() {
        return contact;
    }
}
