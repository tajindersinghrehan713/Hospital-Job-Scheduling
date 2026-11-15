
package hospital;

public class Resource {

    private int resourceId;
    private int departmentId;
    private String resourceName;
    private String resourceType;
    private String availabilityStatus;

    public Resource(int resourceId, int departmentId,
                    String resourceName, String resourceType,
                    String availabilityStatus) {
        this.resourceId = resourceId;
        this.departmentId = departmentId;
        this.resourceName = resourceName;
        this.resourceType = resourceType;
        this.availabilityStatus = availabilityStatus;
    }

    public int getResourceId() {
        return resourceId;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getResourceType() {
        return resourceType;
    }

    public String getAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(String availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }
}