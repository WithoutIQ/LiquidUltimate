package net.ccbluex.liquidbounce.utils.animation;

import java.awt.*;

public class ColorAnimation {
    Animation r = new Animation();
    Animation g = new Animation();
    Animation b = new Animation();
    Animation a = new Animation();
    boolean first = true;
    Color end;

    public ColorAnimation() {
    }

    public ColorAnimation(Color color) {
        setColor(color);
    }

    public ColorAnimation(int red, int green, int blue, int alpha) {
        setColor(new Color(red, green, blue, alpha));
    }

    public void start(Color start, Color end, float duration, Type type) {
        this.end = end;
        r.start(start.getRed(), end.getRed(), duration, type);
        g.start(start.getGreen(), end.getGreen(), duration, type);
        b.start(start.getBlue(), end.getBlue(), duration, type);
        a.start(start.getAlpha(), end.getAlpha(), duration, type);
    }

    public void update() {
        if (end != null) {
            if (first) {
                setColor(end);
                first = false;
                return;
            }
            r.update();
            g.update();
            b.update();
            a.update();
        }
    }

    public void reset() {
        r.reset();
        g.reset();
        b.reset();
        a.reset();
    }

    public Color getColor() {
        return new Color(limit(r.getValue()), limit(g.getValue()), limit(b.getValue()), limit(a.getValue()));
    }


    public static int limit(double i) {
        if (i > 255)
            return 255;
        if (i < 0)
            return 0;
        return (int) i;
    }

    public void fstart(Color color, Color color1, float duration, Type type) {
        this.end = color1;
        r.fstart(color.getRed(), color1.getRed(), duration, type);
        g.fstart(color.getGreen(), color1.getGreen(), duration, type);
        b.fstart(color.getBlue(), color1.getBlue(), duration, type);
        a.fstart(color.getAlpha(), color1.getAlpha(), duration, type);
    }

    public void setColor(Color color) {
        r.value = color.getRed();
        g.value = color.getGreen();
        b.value = color.getBlue();
        a.value = color.getAlpha();
    }

    public void base(Color color) {
        r.value = AnimationUtils.base(r.value, color.getRed(), 0.1f);
        g.value = AnimationUtils.base(g.value, color.getGreen(), 0.1f);
        b.value = AnimationUtils.base(b.value, color.getBlue(), 0.1f);
        a.value = AnimationUtils.base(a.value, color.getAlpha(), 0.1f);
    }
}
