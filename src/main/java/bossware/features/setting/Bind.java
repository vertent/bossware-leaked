package bossware.features.setting;

import bossware.BossWare;
import com.google.common.base.Converter;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class Bind {
    private int bind;
    private BindType type;

    public Bind(int key) {
        this.bind = key;
        this.type = BindType.KEY;
    }

    public Bind(int bind, BindType type) {
        this.bind = bind;
        this.type = type;
    }

    public static Bind none() {
        return new Bind(-1);
    }

    public int getBind() {
        return this.bind;
    }

    public BindType getType() { return this.type; }

    public void setBind(int bind) {
        this.bind = bind;
    }

    public boolean isEmpty() {
        return this.bind < 0;
    }

    public String toString() {
        //No bind
        if(this.isEmpty()) {
            return "None";
        }

        //Bind set to a key
        if(this.type == BindType.KEY) {
            String keyName = Keyboard.getKeyName(bind);
            return this.capitalise(keyName);
        }

        //Bind set to a mouse button
        return "MOUSE" + String.valueOf(bind);
    }

    public boolean isDown() {
        if(this.isEmpty()) {
            return false;
        }

        if(this.type == BindType.KEY) {
            return Keyboard.isKeyDown(bind);
        }

        return Mouse.isButtonDown(bind);
    }

    private String capitalise(String str) {
        if (str.isEmpty()) {
            return "";
        }
        return Character.toUpperCase(str.charAt(0)) + (str.length() != 1 ? str.substring(1).toLowerCase() : "");
    }

    public enum BindType {
        KEY,
        MOUSE
    }

    public static class BindConverter
            extends Converter<Bind, JsonElement> {
        public JsonElement doForward(Bind bind) {
            return new JsonPrimitive(bind.toString());
        }

        public Bind doBackward(JsonElement jsonElement) {
            String s = jsonElement.getAsString();
            BindType type = BindType.KEY;

            if (s.equalsIgnoreCase("None")) {
                return Bind.none();
            }

            if(s.startsWith("MOUSE")) {
                BossWare.LOGGER.info("mouse");
                type = BindType.MOUSE;
                s = s.replace("MOUSE", "");
            }

            int bind = -1;
            try {
                if(type == BindType.KEY) {
                    bind = Keyboard.getKeyIndex(s.toUpperCase());
                } else {
                    BossWare.LOGGER.info(Integer.valueOf(s));
                    bind = Integer.valueOf(s);
                }
            } catch (Exception exception) {
                // empty catch block
            }
            if (bind == 0 && type == BindType.KEY) {
                return Bind.none();
            }
            return new Bind(bind, type);
        }
    }
}

