package dd.util;

public class NetworkInfo {
    public String Type = "";
    public String IP = "";
    public String Gateway = "";
    public String Mask = "";
    public String DNS = "";
    public String Mac = "";

    public String toString() {
        return "{\"Type\":\"" + Type +
                "\",\"IP\":\"" + IP +
                "\",\"Gateway\":\"" + Gateway +
                "\",\"Mask\":\"" + Mask +
                "\",\"DNS\":\"" + DNS +
                "\",\"Mac\":\"" + Mac + "\"}";
    }
}
