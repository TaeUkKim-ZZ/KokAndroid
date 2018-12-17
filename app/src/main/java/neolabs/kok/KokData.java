package neolabs.kok;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class KokData {

    @SerializedName("location")
    @Expose
    private Location location;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("comments")
    @Expose
    private List<Object> comments = null;
    @SerializedName("userauthid")
    @Expose
    private String userauthid;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("__v")
    @Expose
    private Integer v;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Object> getComments() {
        return comments;
    }

    public void setComments(List<Object> comments) {
        this.comments = comments;
    }

    public String getUserauthid() {
        return userauthid;
    }

    public void setUserauthid(String userauthid) {
        this.userauthid = userauthid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getV() {
        return v;
    }

    public void setV(Integer v) {
        this.v = v;
    }

}