package me.tewpingz.core.rank;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

@Data
@AllArgsConstructor
public class RankColor {

    private String hex;
    private boolean bold, italic;

    public void updateColor(int red, int green, int blue) {
        this.hex = TextColor.color(red, green, blue).asHexString();
    }

    public void updateColor(String hex) {
        this.hex = hex;
    }

    public Component apply(Component component) {
        component = component.color(this.toTextColor());

        if (bold) {
            component = component.decorate(TextDecoration.BOLD);
        }

        if (italic) {
            component = component.decorate(TextDecoration.ITALIC);
        }

        return component;
    }

    public TextColor toTextColor() {
        return TextColor.fromHexString(this.hex);
    }
}
