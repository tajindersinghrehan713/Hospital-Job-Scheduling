
package hospital;

public class Staff {

    private int staffId;
    private int departmentId;
    private String name;
    private String role;
    private String availabilityStatus;

    public Staff(int staffId, int departmentId, String name,
                 String role, String availabilityStatus) {
        this.staffId = staffId;
        this.departmentId = departmentId;
        this.name = name;
        this.role = role;
        this.availabilityStatus = availabilityStatus;
    }

    public int getStaffId() {
        return staffId;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public String getAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(String availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }
}
