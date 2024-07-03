import lombok.Getter;

@Getter @SuppressWarnings("unused")
public class MyObject {

    private String name;
    private int count;

    public MyObject() {}
    public MyObject(String name, int count) {
        this.name = name;
        this.count = count;
    }
}
