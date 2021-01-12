package system.api;

import dd.util.U;

public class APIFactory {
    private APIFactory() {
    }

    public static API create(String type) {
        switch (type) {
            case "panel":
                return new YF(U.ACTIVITY);
        }
        return null;
    }
}