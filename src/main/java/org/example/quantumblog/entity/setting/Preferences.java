package org.example.quantumblog.entity.setting;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
//偏好设置
public class Preferences {
    private String color;

    public Preferences() {
    }

    public Preferences(String color) {
        this.color = color;
    }
}
